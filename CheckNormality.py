import matplotlib.pyplot as plt
from scipy import stats
import numpy as np

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
    data = [1.0, 2.1, 2.9, 3.5, 3.7, 4.1, 4.3, 5.0, 5.5, 5.8, 6.0, 6.2, 7.5, 7.9, 8.2, 8.5]
    plot_and_check_distribution(data)
