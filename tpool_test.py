#!/usr/bin/env python3

import subprocess

def run_test(thread_num=1):
    err_count = 0

    print("########### STARTED ############")
    print("Running test case for thread_num = {}".format(thread_num))

    cmd = "./thread_pool/main {}".format(thread_num)
    
    with open("tpool_test.log", 'a') as logfile:
        logfile.write("\n########### STARTED ############\n")
        logfile.write("Running test with command: \"{}\"\n".format(cmd))
        logfile.write('\n')

    try:
        output = subprocess.check_output(cmd,
                                         shell=True,
                                         text=True,
                                         stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as e:
        print("Failed to run ./thread_pool/main: {}".format(e.output))
        
        with open("tpool_test.log", 'a') as logfile:
            logfile.write("########### FAILED #############\n")
            logfile.write(e.output)
            logfile.write('\n')
        
        return

    with open("tpool_test.log", 'a') as logfile:
        logfile.write("############ output ############\n")
        logfile.write(output)
        logfile.write('\n')

    output = output.rstrip().split('\n')

    started_list = set([line for line in output if "started" in line])
    stopped_list = set([line for line in output if "stopped" in line])

    if not (len(started_list) == len(stopped_list) == thread_num):
        with open("tpool_test.log", 'a') as logfile:
            logfile.write("############ ERROR #############\n")
            logfile.write("Thread number don't match\n")

            logfile.write("Started threads: {}\n".format(len(started_list)))
            logfile.write("Stopped threads: {}\n".format(len(stopped_list)))
            logfile.write("thread_num: {}\n".format(thread_num))

            logfile.write('\n')
            err_count += 1
    
    song_list = []
    for line in output:
        if "SONG" in line:
            song_line = line.split(' ', 2)[2]
            if song_line:
                song_list.append(song_line)
    
    song_file_list = []
    with open("song.txt", 'r') as song_file:
        for line in song_file:
            song_line = line.rstrip()
            if song_line:
                song_file_list.append(song_line)

    if sorted(song_list) != sorted(song_file_list):
        with open("tpool_test.log", 'a') as logfile:
            logfile.write("############ ERROR #############\n")
            logfile.write("Produced song and song.txt don't match\n")

            logfile.write("\n#### Sorted produced song:\n")
            for line in sorted(song_list):
                logfile.write("{}\n".format(line))
            logfile.write('\n')

            logfile.write("\n#### Sorted song from song.txt:\n")
            for line in sorted(song_file_list):
                logfile.write("{}\n".format(line))
            logfile.write('\n')

            logfile.write('\n')
            err_count += 1


    with open("tpool_test.log", 'a') as logfile:
        logfile.write("########### FINISHED ###########\n")
        logfile.write("Total error count: {}\n".format(err_count))
        logfile.write("################################\n")

    print("Total error count: {}".format(err_count))
    print("########### FINISHED ###########\n")

# Create/clear logfile
open("tpool_test.log", 'w').close()

run_test(1)
run_test(5)
run_test(10)

print("Check tpool_test.log file for test flow and error descriptions")
