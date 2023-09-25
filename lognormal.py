import csv
import matplotlib.pyplot as plt
from scipy import stats
import numpy as np
import pandas as pd

def read_csv_file(file_path):
    df = pd.read_csv(file_path)
    avg_data = df.mean(axis=1)
    return avg_data.tolist()

def write_result_to_file(file_path, text):
    with open(file_path, 'w') as file:
        file.write(text)

def plot_histogram(data, title, xlabel, ylabel, file_name):
    plt.hist(data, bins='auto', alpha=0.7, color='blue', edgecolor='black')
    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.savefig(file_name)
    plt.show()

def plot_and_check_distribution(data, graph_path, result_path):
    # Calculate additional statistics
    n = len(data)
    mean = np.mean(data)
    variance = np.var(data)

    # Plot histogram of averaged data
    plot_histogram(data, "Histogram of Averaged Data", "Value", "Frequency", f"{graph_path}.png")

    # Chi-Squared Test
    num_bins = int(np.sqrt(n))
    hist, bin_edges = np.histogram(data, bins=num_bins)
    pdf_lognorm = stats.lognorm.pdf(bin_edges, np.sqrt(variance), 0, mean)
    expected_freq = pdf_lognorm * n
    chi_stat, chi_p_value = stats.chisquare(f_obs=hist, f_exp=expected_freq[1:] - expected_freq[:-1])

    alpha = 0.05  # Significance level
    result_text = f"""Chi-Squared Test Result:
    p-value = {chi_p_value}
    Additional Statistics:
    n = {n}
    Mean = {mean}
    Variance = {variance}
    """

    if chi_p_value > alpha:
        result_text += "The data appears to be log-normally distributed (Chi-Squared)."
    else:
        result_text += "The data does not appear to be log-normally distributed (Chi-Squared)."

    print(result_text)
    write_result_to_file(result_path, result_text)

if __name__ == "__main__":
    data = read_csv_file("input.csv")
    plot_and_check_distribution(data, "histogram", "chi_squared_test_results.txt")
