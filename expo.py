import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy import stats

def read_csv_file(filepath):
    """Reads a CSV file and returns the data."""
    return pd.read_csv(filepath)

def write_to_csv(data, filepath):
    """Writes data to a CSV file."""
    data.to_csv(filepath, index=False)

def write_result_to_file(result, filepath):
    """Writes the result of the KS test to a file."""
    with open(filepath, 'w') as f:
        f.write(result)

def plot_histogram(data):
    """Plots a histogram of the data."""
    plt.hist(data, bins='auto', color='skyblue', alpha=0.7, edgecolor='black')
    plt.title("Histogram")
    plt.xlabel("Value")
    plt.ylabel("Frequency")
    plt.show()

def plot_and_check_distribution(data, lambda_exp):
    """Plots the histogram of the data and performs the KS test for an exponential distribution."""
    plot_histogram(data)

    # KS test
    d, p_value = stats.kstest(data, 'expon', args=(0, 1/lambda_exp))

    # Writing the result to a file
    result = f"KS Statistic: {d}, p-value: {p_value}\n"
    if p_value > 0.05:
        result += "The data follows an exponential distribution."
    else:
        result += "The data does not follow an exponential distribution."
    write_result_to_file(result, 'ks_test_result.txt')

    print(result)

if __name__ == "__main__":
    # Example usage:
    filepath = 'data.csv'
    data = read_csv_file(filepath)['Value']  # Assuming the column is named 'Value'

    # Write the same data back to another CSV (optional demonstration)
    write_to_csv(data, 'data_copy.csv')

    # Define the lambda (rate parameter) of the assumed exponential distribution
    lambda_exp = 1.0  # Change as per your data or requirements

    # Plot the histogram and perform the KS test
    plot_and_check_distribution(data, lambda_exp)
