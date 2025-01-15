import os
import sys
import yaml
import json
from lark import Lark, Transformer, v_args

# -----------------------------------------------------------------------------
# Часть 1. Определение Грамматики JSON с Помощью Lark
# -----------------------------------------------------------------------------

json_grammar = r"""
    ?start: value

    ?value: object
          | array
          | STRING
          | SIGNED_NUMBER      -> number
          | "true"             -> true
          | "false"            -> false
          | "null"             -> null

    array  : "[" [value ("," value)*] "]"
    object : "{" [pair ("," pair)*] "}"
    pair   : STRING ":" value

    %import common.ESCAPED_STRING   -> STRING
    %import common.SIGNED_NUMBER
    %import common.WS
    %ignore WS
"""

# -----------------------------------------------------------------------------
# Часть 2. Трансформер для Преобразования Дерева в Структуру Python
# -----------------------------------------------------------------------------

@v_args(inline=True)  # Affects the signatures of the methods
class JsonTransformer(Transformer):
    def __init__(self):
        pass

    def object(self, *pairs):
        return dict(pairs)

    def pair(self, key, value):
        return (key, value)

    def array(self, *values):
        return list(values)

    def string(self, s):
        return s[1:-1].encode().decode('unicode_escape')

    def number(self, n):
        if '.' in n or 'e' in n.lower():
            return float(n)
        else:
            return int(n)

    def true(self):
        return True

    def false(self):
        return False

    def null(self):
        return None

# -----------------------------------------------------------------------------
# Часть 3. Функция для Парсинга JSON и Конвертации в YAML
# -----------------------------------------------------------------------------

def json_to_yaml(json_content):
    parser = Lark(json_grammar, parser='lalr', transformer=JsonTransformer())
    try:
        parsed = parser.parse(json_content)
        # Преобразуем в YAML
        yaml_content = yaml.dump(parsed, allow_unicode=True, sort_keys=False, default_flow_style=False)
        return yaml_content
    except Exception as e:
        print(f"Ошибка при парсинге JSON: {e}")
        sys.exit(1)


def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    json_input_path = os.path.join(script_dir, 'schedule-2.json')
    yaml_output_path = os.path.join(script_dir, 'dop3-output.yaml')

    try:
        data = parse_json(json_input_path)
    except Exception as e:
        print(f"Ошибка при парсинге JSON: {e}")
        return

    convert_to_yaml(data, yaml_output_path)
    print(f"Конвертация завершена! YAML сохранен в '{yaml_output_path}'.")

# -----------------------------------------------------------------------------
# Часть 5. Запуск Скрипта
# -----------------------------------------------------------------------------

main()
