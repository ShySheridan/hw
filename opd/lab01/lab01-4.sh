<<<<<<< HEAD
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
=======
#!/bin/bash
chmod u+r arbok4
chmod u+r combusken6
chmod u+r ledyba
chmod u+r mandibuzz8

echo '#1'
#shopt -s globstar
#wc -m $(ls -dp1 **/*a lab0 | grep -v "/$") | sort -nk1
grep --include='*a' -r . | wc -m | sort -n

#ls -d показывает директории без раскрытия их содержимого
#ls -p добавляет / к имени, если это директория.
#ls -1 выводит один элемент на строку.

echo '#2'
ls -lR lab0 2>/tmp/errors.log | grep "sha" | sort -k2,2nr

echo '#3'
#ls -1 -dp **/*t lab0 | grep -v "/$" | cat $(ls -1 -dp **/*t lab0 | grep -v "/$") | sort 
grep -r --include="*t" $ lab0 | sort -t ':' -k1,1
# $ - чтоб не было проверки на содержание строк

echo '#4'
ls -ltR --time-style=long-iso lab0/weezing2 2>&1 | grep '^-' | sort -k6,7

echo '#5'
ls -lR lab0/weezing2 2>/tmp/errors.log | grep '^-' | sort

echo '#6'
ls -lR lab0 | grep '\-.*t$' | sort -k5,5n

chmod u-r arbok4
chmod u-r combusken6
chmod u-r ledyba
chmod u-r mandibuzz8
>>>>>>> 05acb66d8d1d4163ed9639feb6b44f5bdede816d
