import csv
import matplotlib.pyplot as plt
from scipy import stats
import numpy as np
import pandas as pd

def read_csv_file(file_path):
    df = pd.read_csv(file_path)
    avg_data = df.mean(axis=1)
    return avg_data.tolist()

def write_to_csv(file_path, data):
    with open(file_path, 'w', newline='') as csvfile:
        csv_writer = csv.writer(csvfile)
        for item in data:
            csv_writer.writerow([item])

def write_result_to_file(file_path, text):
    with open(file_path, 'w') as file:
        file.write(text)

def plot_histogram(data, title, xlabel, ylabel, file_name):
    plt.hist(data, bins='auto', alpha=0.5, color='blue', edgecolor='black', density=True, label='Averaged Data')

    # Calculate the rate parameter for Exponential PDF
    rate = 1 / np.mean(data)

    # Generate x values
    x = np.linspace(min(data), max(data), 1000)

    # Generate y values based on Exponential PDF
    pdf_exp = stats.expon.pdf(x, scale=1/rate)

    # Plot Exponential PDF
    plt.plot(x, pdf_exp, 'r-', label='Exponential PDF')

    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.legend()
    plt.savefig(file_name)
    plt.show()

def plot_and_check_distribution(data, graph_path, result_path):
    n = len(data)
    mean = np.mean(data)

    plot_histogram(data, "Histogram with Exponential PDF", "Value", "Density", f"{graph_path}.png")

    num_bins = int(np.sqrt(n))
    hist, bin_edges = np.histogram(data, bins=num_bins, density=True)
    pdf_exp = stats.expon.pdf(bin_edges, scale=1/(1/mean))
    expected_freq = pdf_exp * (bin_edges[1:] - bin_edges[:-1]) * n
    chi_stat, chi_p_value = stats.chisquare(f_obs=hist * (bin_edges[1:] - bin_edges[:-1]) * n, f_exp=expected_freq)

    alpha = 0.05
    result_text = f"""Chi-Squared Test Result:
    p-value = {chi_p_value}
    Additional Statistics:
    n = {n}
    Mean = {mean}
    """

    if chi_p_value > alpha:
        result_text += "The data appears to be exponentially distributed (Chi-Squared)."
    else:
        result_text += "The data does not appear to be exponentially distributed (Chi-Squared)."

    print(result_text)
    write_result_to_file(result_path, result_text)

if __name__ == "__main__":
    data = read_csv_file("input.csv")
    write_to_csv("avg_data_exponential.csv", data)
    plot_and_check_distribution(data, "histogram_exponential", "chi_squared_test_results_exponential.txt")
