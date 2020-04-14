import numpy as np
import matplotlib.pyplot as plt


def main():
    plot_numerical_vs_analytical()


def plot_numerical_vs_analytical():
    gamma = 100
    k = 10000
    mass = 70
    step = 0.001

    times = np.arange(0, 5, step)

    analytical_solution = np.exp(-(gamma/(2*mass))*times)*np.cos(np.sqrt(k/mass - (gamma**2)/(4*(mass**2)))*times)

    label1 = "Gear predictor corrector orden 5"
    label2 = "Beeman"
    label3 = "Verlet - Leap frog"

    plt1, = plot_file("../spring_gear_predictor_corrector", step, label1, analytical_solution)
    plt2, = plot_file("../spring_beeman", step, label2, analytical_solution)
    plt3, = plot_file("../spring_leap_frog", step, label3, analytical_solution)

    plt4, = plt.plot(times, analytical_solution, label="Analítica")

    plt.legend(handles=[plt1, plt2, plt3, plt4])
    plt.show()


def plot_file(filename, step, label, analytical_solution):
    numerical_solution = []
    with open(filename, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]
        for line in lines:
            numerical_solution.append(float(line))

    times = np.arange(0, len(numerical_solution)*step, step)

    errors = [((numerical-analytical)**2) for numerical, analytical in zip(numerical_solution, analytical_solution)]
    mean_squared_error = sum(errors)/len(times)

    print("(%s) Error cuadrático medio: %s" % (label, mean_squared_error))

    return plt.plot(times, numerical_solution, label=label)



if __name__ == "__main__":
    main()