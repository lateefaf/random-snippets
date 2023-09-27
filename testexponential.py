import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy import stats

def read_csv_file(file_path):
    try:
        df = pd.read_csv(file_path)
        return df.mean(axis=1).tolist()
    except Exception as e:
        print(f"An error occurred: {e}")
        return []

def write_to_csv(file_path, data):
    try:
        pd.DataFrame(data, columns=['Averaged Data']).to_csv(file_path, index=False)
    except Exception as e:
        print(f"An error occurred: {e}")

def write_result_to_file(file_path, text):
    try:
        with open(file_path, 'w') as file:
            file.write(text)
    except Exception as e:
        print(f"An error occurred: {e}")

def plot_histogram(data, title, xlabel, ylabel, file_name):
    plt.hist(data, bins='auto', alpha=0.5, color='blue', edgecolor='black', density=True, label='Averaged Data')

    rate = 1 / np.mean(data)
    x = np.linspace(min(data), max(data), 1000)
    pdf_exp = stats.expon.pdf(x, scale=1/rate)

    plt.plot(x, pdf_exp, 'r-', label='Theoretical Exponential PDF')
    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.legend()
    plt.savefig(file_name)
    plt.show()

def perform_chi_squared_test(data, mean):
    n = len(data)
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

    return result_text

def main():
    data = read_csv_file("input.csv")
    if not data:
        return

    write_to_csv("avg_data_exponential.csv", data)
    mean = np.mean(data)

    plot_histogram(data, "Histogram with Theoretical Exponential PDF", "Value", "Density", "histogram_exponential.png")
    chi_squared_result = perform_chi_squared_test(data, mean)

    write_result_to_file("chi_squared_test_results_exponential.txt", chi_squared_result)
    print(chi_squared_result)

if __name__ == "__main__":
    main()
