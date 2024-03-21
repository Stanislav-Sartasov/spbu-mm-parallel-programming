PROC_NUM=4
MAIN_CLASS=Main
INPUT_FILENAME=resources/input.txt

javac -d out -cp .:"$MPJ_HOME"/lib/mpj.jar src/"$MAIN_CLASS".java
mpjrun.sh -np "$PROC_NUM" -cp .:out "$MAIN_CLASS" "$INPUT_FILENAME"
