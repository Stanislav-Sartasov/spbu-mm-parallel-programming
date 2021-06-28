#!/usr/bin/env python3

import subprocess

def run_test(time=5, pruducer_num=2, consumer_num=2):
    err_count = 0

    print("########### STARTED ############")
    print("Running test case for time = {}, pruducer_num = {}, consumer_num = {}".format(
          time, pruducer_num, consumer_num))

    cmd = "timeout --signal=INT --preserve-status {} ./producer_consumer/main {} {}".format(
          time, pruducer_num, consumer_num)
    
    with open("prodcons_test.log", 'a') as logfile:
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
        with open("prodcons_test.log", 'a') as logfile:
            logfile.write("########### FAILED #############\n")
            logfile.write(e.output)
            logfile.write('\n')

    with open("prodcons_test.log", 'a') as logfile:
        logfile.write("############ output ############\n")
        logfile.write(output)
        logfile.write('\n')

    output = output.rstrip().split('\n')

    produce_list = [line for line in output if "PRODUCE " in line]
    consume_list = [line for line in output if "CONSUME " in line]

    producer_closed_list = [line for line in output if "PRODUCER " in line]
    consumer_closed_list = [line for line in output if "CONSUMER " in line]

    remaining_nodes_list = [line for line in output if "NODE " in line]

    produced_numbers  = sorted([int(line.partition('num:')[2]) for line in produce_list])
    consumed_numbers  = sorted([int(line.partition('num:')[2]) for line in consume_list])
    remaining_numbers = sorted([int(line.partition('num:')[2]) for line in remaining_nodes_list])
   
    with open("prodcons_test.log", 'a') as logfile:
        logfile.write("####### produced numbers #######\n")
        logfile.write(', '.join(map(str, produced_numbers)))
        logfile.write('\n')

        logfile.write("\n####### consumed numbers #######\n")
        logfile.write(', '.join(map(str, consumed_numbers)))
        logfile.write('\n')

        logfile.write("\n####### remaining numbers ######\n")
        logfile.write(', '.join(map(str, remaining_numbers)))
        logfile.write('\n')

        logfile.write('\n')

    producer_closed_num = len(producer_closed_list)
    consumer_closed_num = len(consumer_closed_list)
    
    if producer_closed_num != pruducer_num:
        with open("prodcons_test.log", 'a') as logfile:
            logfile.write("############ ERROR #############\n")
            logfile.write("Producer num set: {}\n".format(pruducer_num))
            logfile.write("Producer num closed: {}\n".format(producer_closed_num))

            logfile.write('\n')
            err_count += 1
    
    if consumer_closed_num != consumer_num:
        with open("prodcons_test.log", 'a') as logfile:
            logfile.write("############ ERROR #############\n")
            logfile.write("Consumer num set: {}\n".format(consumer_num))
            logfile.write("Consumer num closed: {}\n".format(consumer_closed_num))

            logfile.write('\n')
            err_count += 1

    consumed_numbers_copy = consumed_numbers.copy()
    for num in consumed_numbers_copy:
        if num in produced_numbers:
            produced_numbers.remove(num)
            consumed_numbers.remove(num)

    if consumed_numbers:
        with open("prodcons_test.log", 'a') as logfile:
            logfile.write("############ ERROR #############\n")
            logfile.write("Consumed numbers that were not produced:\n")
            logfile.write(', '.join(map(str, consumed_numbers)))

            logfile.write('\n')
            err_count += 1

    if produced_numbers != remaining_numbers:
        with open("prodcons_test.log", 'a') as logfile:
            logfile.write("############ ERROR #############\n")
            logfile.write("Produced but not consumed and remaining numbers are not the same\n")
            
            logfile.write("Produced but not consumed numbers:\n")
            logfile.write(', '.join(map(str, produced_numbers)))
            logfile.write('\n')
            
            logfile.write("\nRemaining numbers:\n")
            logfile.write(', '.join(map(str, remaining_numbers)))
            logfile.write('\n')
            
            logfile.write('\n')
            err_count += 1

    with open("prodcons_test.log", 'a') as logfile:
        logfile.write("########### FINISHED ###########\n")
        logfile.write("Total error count: {}\n".format(err_count))
        logfile.write("################################\n")

    print("Total error count: {}".format(err_count))
    print("########### FINISHED ###########\n")

# Create/clear logfile
open("prodcons_test.log", 'w').close()

run_test(10, 1, 1)
run_test(10, 10, 10)
run_test(10, 100, 100)

run_test(10, 1, 10)
run_test(10, 10, 1)

run_test(10, 10, 100)
run_test(10, 100, 10)

print("Check prodcons_test.log file for test flow and error descriptions")
