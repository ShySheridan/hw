#!/bin/bash

# Рекурсивно просматриваем содержимое текущей директории
ls -R | grep -v ":$" | grep -E 'a$' | while read file; do
  # Проверяем, что это файл
  if [[ -f "$file" ]]; then
    # Подсчитываем количество символов в файле
    char_count=$(cat "$file" | wc -m)
    # Выводим количество символов и имя файла
    echo "$char_count $file"
  fi
done | sort -n
