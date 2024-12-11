#!/bin/bash
cd lab0
# 1. Скопировать рекурсивно директорию mandibuzz8 в директорию lab0/mandibuzz8/vileplume
chmod -R u+r mandibuzz8
chmod u+w mandibuzz8/vileplume
cp -r mandibuzz8 mandibuzz8/vileplume

# 2. Создать символическую ссылку для файла arbok4 с именем lab0/mandibuzz8/nidoranFarbok
chmod u+r arbok4
#chmod u+w mandibuzz8
ln -s arbok4 mandibuzz8/nidoranFarbok

# 3. Объединить содержимое файлов lab0/mandibuzz8/nidoranF, lab0/combusken6/musharna в новый файл lab0/ledyba7_65
chmod u+r mandibuzz8/nidoranF
chmod u+r combusken6/musharna
cat mandibuzz8/nidoranF combusken6/musharna > ledyba7_65

# 4. Создать жесткую ссылку для файла ledyba7 с именем lab0/mandibuzz8/nidoranFledyba
chmod u+rx ledyba7
ln ledyba7 mandibuzz8/nidoranFledyba

# 5. Скопировать файл ledyba7 в директорию lab0/combusken6/gengar
#chmod u+rx ledyba7
#chmod u+rw combusken6/gengar
cp ledyba7 combusken6/gengar

# 6. Скопировать содержимое файла ledyba7 в новый файл lab0/mandibuzz8/gligarledyba
#chmod u+r ledyba7
touch mandibuzz8/gigarledyba
cat ledyba7 > mandibuzz8/gligarledyba

# 7. Создать символическую ссылку с именем Copy_76 на директорию mandibuzz8 в каталоге lab0
chmod u+r mandibuzz8
ln -s mandibuzz8 Copy_76

chmod 004 arbok4
chmod u=,g=,o=r ledyba7
chmod u=rw,g=w,o= mienshao0

chmod 357 combusken6/altaria
chmod u=r,g=r,o= combusken6/musharna
chmod 330 combusken6/abomasnow
chmod u=wx,g=wx,o=rx combusken6/gengar
chmod u=rx,g=rwx,o=w combusken6/rattata
chmod u=rwx,g=wx,o=rw combusken6/emboar
chmod 355 combusken6

chmod u=rwx,g=wx,o=wx mandibuzz8/vileplume
chmod u=r,g=,o= mandibuzz8/nidoranF
chmod 444 mandibuzz8/trapinch
chmod 321 mandibuzz8/dragonair
chmod 440 mandibuzz8/gligar
chmod 315 mandibuzz8

chmod 710 weezing2/kricketot
chmod 660 weezing2/gorebyss
chmod 571 weezing2/magcargo
chmod 631 weezing2/swalot
chmod 440 weezing2/koffing
chmod 733 weezing2