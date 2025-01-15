import os
import re


WHITESPACE = re.compile(r'\s+')
SYNTAX = {
    '{': 'LEFT_BRACE',
    '}': 'RIGHT_BRACE',
    '[': 'LEFT_BRACKET',
    ']': 'RIGHT_BRACKET',
    ',': 'COMMA',
    ':': 'COLON'
}

STRING_REGEX = re.compile(r'^"([^"\\]*)"')
NUMBER_REGEX = re.compile(r'^\d+')
BOOL_REGEX = re.compile(r'^(true|false)\b')
NULL_REGEX = re.compile(r'^null\b')

def tokenize(s):
    # Превращает строку JSON в последовательность токенов с использованием регулярных выражений.
    
    tokens = []
    i = 0
    length = len(s)

    while i < length:
        substring = s[i:]

        ws_match = WHITESPACE.match(substring)
        if ws_match:
            i += ws_match.end()
            continue

        char = substring[0]
        if char in SYNTAX:
            tokens.append(SYNTAX[char])
            i += 1
            continue

        string_match = STRING_REGEX.match(substring)
        if string_match:
            # string_value = bytes(string_match.group(1), "utf-8").decode("unicode_escape")
            # string_value = string_match.group(1).encode().decode('unicode_escape')
            string_value = string_match.group(1)
            tokens.append(f"STRING:{string_value}")
            i += string_match.end()
            continue

        number_match = NUMBER_REGEX.match(substring)
        if number_match:
            number_str = number_match.group(0)
            tokens.append(f"NUMBER:{number_str}")
            i += number_match.end()
            continue

        bool_match = BOOL_REGEX.match(substring)
        if bool_match:
            bool_str = bool_match.group(1)
            tokens.append(f"BOOL:{bool_str}")
            i += bool_match.end()
            continue

        null_match = NULL_REGEX.match(substring)
        if null_match:
            tokens.append("NULL")
            i += null_match.end()
            continue

        raise ValueError(f"Неизвестный символ '{char}' на позиции {i}")

    return tokens


def parse_json(s):
    # получает строку JSON -> токенизирует -> парсит -> возвращает Python-объект.

    list_of_tokens = tokenize(s)
    result, remaining = parse_tokens(list_of_tokens, 0)
    if remaining != len(list_of_tokens):
        raise ValueError("Остались непрочитанные токены")
    return result

def parse_tokens(tokens, i):
    # Разбирает список токенов, начиная с позиции i и возвращает кортеж (полученный объект, новая позиция).

    if i >= len(tokens):
        raise ValueError("Выход за границу списка токенов")

    current = tokens[i]

    if current == 'LEFT_BRACE':
        return parse_object(tokens, i + 1)

    elif current == 'LEFT_BRACKET':
        return parse_array(tokens, i + 1)

    elif current.startswith("STRING:"):
        return current.split("STRING:", 1)[1], i + 1

    elif current.startswith("NUMBER:"):
        num_str = current.split("NUMBER:", 1)[1]
        if '.' in num_str or 'e' in num_str.lower():
            return float(num_str), i + 1
        else:
            return int(num_str), i + 1

    elif current.startswith("BOOL:"):
        val_str = current.split("BOOL:", 1)[1]
        return (True if val_str == 'true' else False), i + 1

    elif current == 'NULL':
        return None, i + 1

    else:
        raise ValueError(f"Неожиданный токен '{current}' на позиции {i}")

def parse_object(tokens, i):
    # Парсим объект (после '{').
    obj = {}
    # Рассматриваем пустой объект {}
    if i < len(tokens) and tokens[i] == 'RIGHT_BRACE':
        return obj, i + 1

    while True:
        if i >= len(tokens):
            raise ValueError("Выход за границу списка токенов")

        # Ожидаем STRING:key
        key_token = tokens[i]
        if not key_token.startswith("STRING:"):
            raise ValueError(f"Ожидалась строка, получили {key_token}")
        key = key_token.split("STRING:", 1)[1]
        i += 1

        # Ожидаем ':'
        if i >= len(tokens) or tokens[i] != 'COLON':
            raise ValueError("Ожидалось ':' после ключа")
        i += 1  # пропускаем 'COLON'

        value, i = parse_tokens(tokens, i)
        obj[key] = value

        # Либо конец объекта, либо запятая
        if i >= len(tokens):
            raise ValueError("Выход за границу списка токенов при парсинге объекта")

        if tokens[i] == 'RIGHT_BRACE':
            return obj, i + 1
        elif tokens[i] == 'COMMA':
            i += 1
            continue
        else:
            raise ValueError(f"Ожидалась ',' или '}}', получили {tokens[i]}")

def parse_array(tokens, i):
    # Парсим массив (после '[').
    arr = []

    if i < len(tokens) and tokens[i] == 'RIGHT_BRACKET':
        return arr, i + 1

    while True:
        if i >= len(tokens):
            raise ValueError("Выход за границу списка токенов при парсинге массива")

        value, i = parse_tokens(tokens, i)
        arr.append(value)

        if i >= len(tokens):
            raise ValueError("Выход за границу списка токенов при парсинге массива")

        if tokens[i] == 'RIGHT_BRACKET':
            return arr, i + 1
        elif tokens[i] == 'COMMA':
            i += 1
            continue
        else:
            raise ValueError(f"Ожидалась ',' или ']', получили {tokens[i]}")


def format_scalar(value):
    if value is True:
        return "true"
    elif value is False:
        return "false"
    elif value is None:
        return "null"
    else:
        # Если строка содержит специальные символы, пробелы или начинается с цифры, оборачиваем в кавычки
        if isinstance(value, str):
            if re.search(r'[,:{}\[\]#&*!|>\'"`]', value) or re.match(r'^\d', value):
                # Экранируем внутренние кавычки
                escaped = value.replace('"', '\\"')
                return f'"{escaped}"'
        return str(value)

def to_yaml(data, indent=0):
 
    def spaces(n):
        return ' ' * n

    if isinstance(data, dict):
        lines = []
        for key, value in data.items():
            if isinstance(value, (dict, list)):
                # Вложенная структура
                lines.append(f"{spaces(indent)}{key}:")
                lines.append(to_yaml(value, indent + 2))
            else:
                # Простой элемент в одну строку
                lines.append(f"{spaces(indent)}{key}: {format_scalar(value)}")
        return '\n'.join(lines)

    elif isinstance(data, list):
        lines = []
        for item in data:
            if isinstance(item, dict):
                # Ищем первый ключ для компактного вывода с дефисом
                item_keys = list(item.keys())
                if len(item_keys) > 0:
                    first_key = item_keys[0]
                    first_value = item[first_key]
                    # Формируем строку с дефисом и первым ключом
                    line = f"{spaces(indent)}- {first_key}: {format_scalar(first_value)}"
                    lines.append(line)

                    # Создаём копию словаря без первого ключа
                    temp_dict = dict(item)
                    temp_dict.pop(first_key)

                    if len(temp_dict) > 0:
                        # Рекурсивно обрабатываем оставшиеся ключи с увеличенным отступом
                        sub_yaml = to_yaml(temp_dict, indent + 2)
                        lines.append(sub_yaml)
                else:
                    # Пустой словарь
                    lines.append(f"{spaces(indent)}- {{}}")
            elif isinstance(item, list):
                # Вложенный список
                lines.append(f"{spaces(indent)}-")
                lines.append(to_yaml(item, indent + 2))
            else:
                # Скалярное значение
                lines.append(f"{spaces(indent)}- {format_scalar(item)}")
        return '\n'.join(lines)

    else:
        # Скаляр
        return f"{spaces(indent)}{format_scalar(data)}"


def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    json_file_path = os.path.join(script_dir, 'schedule.json')

    with open(json_file_path, 'r', encoding='utf-8') as f:
        json_string = f.read()

    try:
        data = parse_json(json_string)
    except ValueError as ve:
        print(f"Ошибка при парсинге JSON: {ve}")
        return

    yaml_str = "---\n" + to_yaml(data)

    print(yaml_str)


main()
