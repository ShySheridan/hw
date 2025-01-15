import os

WHITESPACE = (' ', '\t', '\n', '\r')
SYNTAX = {
    '{': 'LEFT_BRACE',
    '}': 'RIGHT_BRACE',
    '[': 'LEFT_BRACKET',
    ']': 'RIGHT_BRACKET',
    ',': 'COMMA',
    ':': 'COLON'
}

def tokenize(s):
    # превращает строку JSON в последовательность токенов

    tokens = []
    i = 0
    length = len(s)

    while i < length:
        char = s[i]

        if char in WHITESPACE:
            i += 1
            continue

        if char in SYNTAX:
            tokens.append(SYNTAX[char])  
            i += 1
            continue

        if char == '"':
            i += 1  # пропускаем саму кавычку
            start = i
            while i < length and s[i] != '"':
                i += 1
            if i >= length:
                raise ValueError("незавершенная строка")
            
            string_value = s[start:i]
            tokens.append(f"STRING:{string_value}")
            i += 1  # пропускаем закрывающую кавычку
            continue

        if char.isdigit():
            start = i
            i += 1
            while i < length and s[i].isdigit():
                i += 1
            number_str = s[start:i]
            tokens.append(f"NUMBER:{number_str}")
            continue

        if s.startswith('true', i):
            tokens.append("BOOL:true")
            i += 4
            continue
        if s.startswith('false', i):
            tokens.append("BOOL:false")
            i += 5
            continue
        if s.startswith('null', i):
            tokens.append("NULL")
            i += 4
            continue

        raise ValueError(f"неизвестный символ '{char}' на позиции {i}")

    return tokens



def parse_json(s):
    # принимает строку JSON, токенизирует и парсит её,
    # возвращая Python-объект (dict, list, str, int/float, bool, None)
    
    list_of_tokens = tokenize(s)
    result, remaining = parse_tokens(list_of_tokens, 0)
    if remaining != len(list_of_tokens):
        raise ValueError("остались непрочитанные токены")
    return result

def parse_tokens(tokens, i):
    # разбирает список токенов и возвращает кортеж (полученный объект, новая_позиция)

    if i >= len(tokens):
        raise ValueError("выход за границу списка токенов")

    current = tokens[i]

    if current == 'LEFT_BRACE':
        return parse_object(tokens, i + 1)

    elif current == 'LEFT_BRACKET':
        return parse_array(tokens, i + 1)

    elif current.startswith("STRING:"):
        return current.split("STRING:", 1)[1], i + 1

    elif current.startswith("NUMBER:"):
        num_str = current.split("NUMBER:", 1)[1]
        return int(num_str), i + 1

    elif current.startswith("BOOL:"):
        val_str = current.split("BOOL:", 1)[1]
        return (True if val_str == 'true' else False), i + 1

    elif current == 'NULL':
        return None, i + 1


def parse_object(tokens, i):
    # парсим объект (после '{')
    obj = {}
    # рассматриваем пустой объект {}
    if i < len(tokens) and tokens[i] == 'RIGHT_BRACE':
        return obj, i + 1

    while True:
        if i >= len(tokens):
            raise ValueError("выход за границу списка токенов")

        # Ожидаем STRING:key
        key_token = tokens[i]
        if not key_token.startswith("STRING:"):
            raise ValueError(f"ожидалось слово, получили {key_token}")
        key = key_token.split("STRING:", 1)[1]
        i += 1

        # Ожидаем ':'
        if i >= len(tokens) or tokens[i] != 'COLON':
            raise ValueError("ожидалось ':' после ключа")
        i += 1  # пропускаем 'COLON'

        # Теперь парсим значение
        value, i = parse_tokens(tokens, i)
        obj[key] = value

        # Либо конец объекта, либо запятая
        if i >= len(tokens):
            raise ValueError("выход за границу списка токенов")

        if tokens[i] == 'RIGHT_BRACE':
            return obj, i + 1
        elif tokens[i] == 'COMMA':
            i += 1
            continue
        else:
            raise ValueError(f"ожидалась ',' или '}}', получили {tokens[i]}")

def parse_array(tokens, i):
    
    arr = []
    # Может быть пустой массив: [ ]
    if i < len(tokens) and tokens[i] == 'RIGHT_BRACKET':
        return arr, i + 1

    while True:
        if i >= len(tokens):
            raise ValueError("выход за границу списка токенов")

        # Парсим элемент
        value, i = parse_tokens(tokens, i)
        arr.append(value)

        if i >= len(tokens):
            raise ValueError("выход за границу списка токенов")

        if tokens[i] == 'RIGHT_BRACKET':
            return arr, i + 1
        elif tokens[i] == 'COMMA':
            i += 1
            continue
        else:
            raise ValueError(f"ожидалась ',' или ']', получили {tokens[i]}")



def format_scalar(value):
    # преобразует простое значение (число, строку, bool, None) в строку
    
    if value is True:
        return "true"
    elif value is False:
        return "false"
    elif value is None:
        return "null"
    else:
        return str(value)

def to_yaml(data, indent=0):
    """
    рекурсивная функция, которая обходит Python-объект (dict, list, скаляр)
    и формирует YAML-подобную строку
    """
    def spaces(n):
        return ' ' * n

    if isinstance(data, dict):
        # Словарь
        lines = []
        for key, value in data.items():
            if isinstance(value, (dict, list)):
                # вложенная структура
                lines.append(f"{spaces(indent)}{key}:")
                lines.append(to_yaml(value, indent + 2))
            else:
                # Простой элемент в одну строку
                lines.append(f"{spaces(indent)}{key}: {format_scalar(value)}")
        return '\n'.join(lines)

    elif isinstance(data, list):
        lines = []
        for item in data:
            if isinstance(item, (dict, list)):
                lines.append(f"{spaces(indent)}-")
                lines.append(to_yaml(item, indent + 2))
            else:
                lines.append(f"{spaces(indent)}- {format_scalar(item)}")
        return '\n'.join(lines)

    else:
        # Скаляр
        return f"{spaces(indent)}{format_scalar(data)}"



def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    json_file_path = os.path.join(script_dir, 'schedule-2.json')

    with open(json_file_path, 'r', encoding='utf-8') as f:
        json_string = f.read()

    data = parse_json(json_string)

    yaml_str = "---\n" + to_yaml(data)

    print(yaml_str)


main()
