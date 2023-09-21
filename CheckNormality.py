import csv
import matplotlib.pyplot as plt
from scipy import stats
import numpy as np

def read_csv_file(file_path):
    data = []
    with open(file_path, 'r') as csvfile:
        csv_reader = csv.reader(csvfile)
        for row in csv_reader:
            data.append(float(row[0]))
    return data

def plot_and_check_distribution(data):
    # Plot histogram
    plt.hist(data, bins='auto', alpha=0.7, color='blue', edgecolor='black')
    plt.title("Histogram of Given Data")
    plt.xlabel("Value")
    plt.ylabel("Frequency")
    plt.show()

    # Perform Shapiro-Wilk test for normality
    stat, p_value = stats.shapiro(data)
    alpha = 0.05  # Significance level
    if p_value > alpha:
        print(f"The data appears to be normally distributed (p-value = {p_value}).")
    else:
        print(f"The data does not appear to be normally distributed (p-value = {p_value}).")

if __name__ == "__main__":
    data = read_csv_file("input.csv")
    plot_and_check_distribution(data)
