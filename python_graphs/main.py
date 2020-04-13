import numpy as np
import matplotlib.pyplot as plt


def main():
    plot_positions()


def plot_positions():
    filename = "../output"

    values = []
    with open(filename, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]
        for line in lines:
            values.append(float(line))

    times = np.arange(0, len(values), 1)

    plt.plot(times, values)
    plt.show()


if __name__ == "__main__":
    main()