import numpy as np
import matplotlib.pyplot as plt


def main():
    plot_numerical_vs_analytical()


def plot_numerical_vs_analytical():
    gamma = 1000
    k = 10000
    mass = 70
    step = 0.001

    filename = "../output"

    numerical_solution = []
    with open(filename, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]
        for line in lines:
            numerical_solution.append(float(line))

    times = np.arange(0, len(numerical_solution)*step, step)

    # Solution for overdamped oscillator
    analytical_solution = np.exp(-(gamma/(2*mass))*times)*np.cos(np.sqrt(k/mass - (gamma**2)/(4*(mass**2)))*times)

    plt.plot(times, analytical_solution)
    plt.plot(times, numerical_solution)

    plt.show()

    errors = [((numerical-analytical)**2) for numerical, analytical in zip(numerical_solution, analytical_solution)]
    mean_squared_error = sum(errors)/len(times)
    
    print("Error cuadratico medio: %s" % mean_squared_error)


if __name__ == "__main__":
    main()