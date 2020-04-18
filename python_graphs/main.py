import os

import numpy as np
import matplotlib.pyplot as plt


def main():
    plot_numerical_vs_analytical()
    plot_error_vs_step()


def plot_error_vs_step():
    label1 = "Gear predictor corrector orden 5"
    label2 = "Beeman"
    label3 = "Verlet - Leap frog"

    parameters = {
        "gamma": 100,
        "k": 10000,
        "mass": 70
    }

    plt1, = plot_file_errors_vs_step("../results/spring/gear_predictor_corrector/", label1, parameters)
    plt2, = plot_file_errors_vs_step("../results/spring/beeman/", label2, parameters)
    plt3, = plot_file_errors_vs_step("../results/spring/leap_frog/", label3, parameters)

    plt.xscale('log')
    plt.yscale('log')
    plt.legend(handles=[plt1, plt2, plt3])
    plt.show()


def plot_file_errors_vs_step(directory, label, parameters):
    tuples = []

    for filename in os.listdir(directory):
        numerical_solution = []

        step = 0
        with open(directory + filename, 'r') as f:  # open in readonly mode
            lines = [line.rstrip() for line in f]
            step = float(lines[0])

            for line in lines[1:]:
                numerical_solution.append(float(line.split(" ")[0]))

        times = np.arange(0, len(numerical_solution)*step, step)

        analytical_solution = np.exp(-(parameters["gamma"]/(2*parameters["mass"]))*times)*np.cos(np.sqrt(parameters["k"]/parameters["mass"] - (parameters["gamma"]**2)/(4*(parameters["mass"]**2)))*times)

        errors = [((numerical-analytical)**2) for numerical, analytical in zip(numerical_solution, analytical_solution)]
        mean_squared_error = sum(errors)/len(times)

        tuples.append((step, mean_squared_error))

    tuples.sort(key=lambda tup: tup[0])
    return plt.plot([x for x, y in tuples], [y for x, y in tuples], label=label)


def plot_numerical_vs_analytical():
    gamma = 100
    k = 10000
    mass = 70

    step = 0
    with open("../results/spring/gear_predictor_corrector/sim2", 'r') as f:
        step = float(f.readline())

    times = np.arange(0, 5, step)

    analytical_solution = np.exp(-(gamma/(2*mass))*times)*np.cos(np.sqrt(k/mass - (gamma**2)/(4*(mass**2)))*times)

    label1 = "Gear predictor corrector orden 5"
    label2 = "Beeman"
    label3 = "Verlet - Leap frog"

    plt1, = plot_file_numerical_vs_analytical("../results/spring/gear_predictor_corrector/sim2", step, label1, analytical_solution)
    plt2, = plot_file_numerical_vs_analytical("../results/spring/beeman/sim2", step, label2, analytical_solution)
    plt3, = plot_file_numerical_vs_analytical("../results/spring/leap_frog/sim2", step, label3, analytical_solution)

    plt4, = plt.plot(times, analytical_solution, label="Analítica")

    plt.legend(handles=[plt1, plt2, plt3, plt4])
    plt.show()


def plot_file_numerical_vs_analytical(filename, step, label, analytical_solution):
    numerical_solution = []
    with open(filename, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]
        for line in lines[1:]:
            numerical_solution.append(float(line.split(" ")[0]))

    times = np.arange(0, len(numerical_solution)*step, step)

    errors = [((numerical-analytical)**2) for numerical, analytical in zip(numerical_solution, analytical_solution)]
    mean_squared_error = sum(errors)/len(times)

    print("(%s) Error cuadrático medio: %s" % (label, mean_squared_error))

    return plt.plot(times, numerical_solution, label=label)



if __name__ == "__main__":
    main()