#!/bin/bash

# Function to check if Docker container is fully up and running
wait_for_container() {
  local container_name=$1
  local max_retries=10
  local retries=0

  echo "Waiting for the container $container_name to be fully up and running..."

  while [ $retries -lt $max_retries ]; do
    container_status=$(docker inspect -f '{{.State.Running}}' $container_name)
    if [ "$container_status" == "true" ]; then
      echo "Container $container_name is up and running."
      return 0
    fi

    echo "Waiting for the container $container_name to start..."
    sleep 5
    retries=$((retries + 1))
  done

  echo "Failed to start the container $container_name within the expected time."
  docker logs $container_name
  return 1
}

# Function to check if the application inside the container is ready
wait_for_application() {
  local retries=10
  while [ $retries -gt 0 ]; do
    if curl -s http://localhost:8080/api/health | grep -q "UP"; then
      echo "Application is up and running"
      return 0
    else
      echo "Waiting for the application to be ready..."
      sleep 5
    fi
    retries=$((retries - 1))
  done

  echo "Application did not start within the expected time."
  return 1
}

# Function to run JMeter tests
run_jmeter_test() {
  local implementation=$1
  local rps=$2
  local test_plan="jmeter/load_test_${rps}rps.jmx"
  local results_file="jmeter/results/jmeter_results_${implementation}_${rps}rps.csv"
  local container_name="exam-system-${implementation}"

  local set_impl=""
  if [ "$implementation" == "lazy" ]; then
    set_impl="-e SET_IMPL=lazy"
  fi

  echo "Starting Docker container for $implementation implementation..."
  # Run Docker container
  docker run -d -p 8080:8080 $set_impl --name $container_name exam-system

  # Wait for the container to be fully up and running
  if ! wait_for_container $container_name; then
    echo "Failed to start the $implementation container."
    exit 1
  fi

  # Wait for the application to be ready
  if ! wait_for_application; then
    echo "Application in the container $container_name did not become ready."
    exit 1
  fi

  echo "Running JMeter test plan: $test_plan"
  # Run JMeter test plan
  ../apache-jmeter-5.6.1/bin/jmeter -n -t $test_plan -l $results_file

  echo "Stopping Docker container: $container_name"
  # Stop and remove Docker container
  docker stop $container_name
  docker rm $container_name

  echo "JMeter test completed for $implementation with $rps RPS. Results saved to $results_file"
}

# Create results directory if it doesn't exist
mkdir -p jmeter/results

# Run tests for both implementations and different RPS values
for implementation in default lazy; do
  for rps in 1 100 40000; do
    run_jmeter_test $implementation $rps
  done
done

