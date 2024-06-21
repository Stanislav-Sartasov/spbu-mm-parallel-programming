#!/bin/bash

# Проверка операционной системы
unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)     OS=Linux;;
    Darwin*)    OS=Mac;;
    CYGWIN*)    OS=Cygwin;;
    MINGW*)     OS=MinGw;;
    *)          OS="UNKNOWN:${unameOut}"
esac

# Количество процессов MPI
PROC_NUM=4

# Главный класс вашего приложения Java
MAIN_CLASS=ParallelSort

# Проверка системы и настройка переменных для компиляции и запуска
if [ "$OS" == "Linux" ] || [ "$OS" == "Mac" ]; then
    # Для Linux и Mac
    JAVA_CMD="javac"
    MPJRUN_CMD="mpjrun.sh"
else
    # Для Windows (Cygwin или MinGw)
    JAVA_CMD="javac"
    MPJRUN_CMD="mpjrun.bat"
fi

# Генерация данных
python data_generator.py

# Компиляция Java-кода
"$JAVA_CMD" -cp "$MPJ_HOME/lib/mpj.jar" "$MAIN_CLASS.java" ArrayUtils.java Result.java

# Запуск MPI-приложения
"$MPJRUN_CMD" -np "$PROC_NUM" "$MAIN_CLASS"

# Ожидание ввода от пользователя, чтобы окно консоли не закрылось
read -p "Нажмите Enter для выхода..."

