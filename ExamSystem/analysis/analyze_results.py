import pandas as pd
import matplotlib.pyplot as plt

def load_and_prepare_data(file_path):
    data = pd.read_csv(file_path)
    data = data[['timeStamp', 'elapsed', 'label', 'success', 'responseCode']]
    return data

def plot_response_time_distribution(data, title, output_file):
    data['response_time'] = data['elapsed']
    data['status'] = data['responseCode'].apply(lambda x: 'OK' if x == 200 else 'KO')

    plt.figure(figsize=(12, 6))
    colors = {'OK': 'blue', 'KO': 'orange'}
    for status, group_data in data.groupby('status'):
        plt.hist(group_data['response_time'], bins=50, alpha=0.7, label=status, color=colors[status])

    plt.title(title)
    plt.xlabel('Response Time (ms)')
    plt.ylabel('Percentage of Requests')
    plt.legend(loc='upper right')
    plt.savefig(output_file)
    plt.close()

def analyze_results():
    strategies = ['default', 'lazy']
    load_levels = ['1rps', '100rps', '40000rps']

    for strategy in strategies:
        for load_level in load_levels:
            file_path = f'jmeter/results/jmeter_results_{strategy}_{load_level}.csv'
            data = load_and_prepare_data(file_path)
            title = f'Response Time Distribution ({strategy.capitalize()} - {load_level})'
            output_file = f'analysis/response_time_distribution_{strategy}_{load_level}.png'
            plot_response_time_distribution(data, title, output_file)

if __name__ == "__main__":
    analyze_results()

