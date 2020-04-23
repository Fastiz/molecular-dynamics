import os

import numpy as np
import matplotlib.pyplot as plt


def main():
    #plot_numerical_vs_analytical()
    plot_error_vs_step()
    plot_min_distance_from_mars_all_times()


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

        times = np.arange(0, len(numerical_solution) * step, step)

        analytical_solution = np.exp(-(parameters["gamma"] / (2 * parameters["mass"])) * times) * np.cos(np.sqrt(
            parameters["k"] / parameters["mass"] - (parameters["gamma"] ** 2) / (
                        4 * (parameters["mass"] ** 2))) * times)

        errors = [((numerical - analytical) ** 2) for numerical, analytical in
                  zip(numerical_solution, analytical_solution)]
        mean_squared_error = sum(errors) / len(times)

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

    analytical_solution = np.exp(-(gamma / (2 * mass)) * times) * np.cos(
        np.sqrt(k / mass - (gamma ** 2) / (4 * (mass ** 2))) * times)

    label1 = "Gear predictor corrector orden 5"
    label2 = "Beeman"
    label3 = "Verlet - Leap frog"

    plt1, = plot_file_numerical_vs_analytical("../results/spring/gear_predictor_corrector/sim2", step, label1,
                                              analytical_solution)
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

    times = np.arange(0, len(numerical_solution) * step, step)

    errors = [((numerical - analytical) ** 2) for numerical, analytical in zip(numerical_solution, analytical_solution)]
    mean_squared_error = sum(errors) / len(times)

    print("(%s) Error cuadrático medio: %s" % (label, mean_squared_error))

    return plt.plot(times, numerical_solution, label=label)


def plot_min_distance_from_mars_all_times():
    step = 1000 * 100
    iterations = 100
    directory = "../results/gravity/"

    times = np.arange(step, step * (iterations+1), step)
    min_distances = []

    for filename in os.listdir(directory):
        min_distances.append(min_distance_from_mars(directory + filename))

    plt.yscale('log')
    plt.plot(times, min_distances)
    plt.axhline(y=3389.5, xmin=min(times), xmax=max(times), color='r')

    min_val = min(min_distances)
    index = min_distances.index(min_val)

    plot_distance_from_mars_vs_time("../results/gravity/sim"+str(int(times[index]/step)))

    plt.show()


def min_distance_from_mars(path):
    lines = []
    with open(path, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]

    launching_time = int(lines[0])

    offset = 1 + launching_time * 4

    min_distance = -1

    mars_index = 3
    spaceship_index = 4

    for index in range(int((len(lines)-offset) / 5)):
        temp_values = lines[index*5+offset:index*5 + 5 + offset]
        spaceship_x, spaceship_y = temp_values[spaceship_index].split()
        mars_x, mars_y = temp_values[mars_index].split()

        dist = np.linalg.norm(np.array([float(spaceship_x), float(spaceship_y)]) - np.array([float(mars_x),
                                                                                             float(mars_y)]))

        if min_distance == -1 or min_distance > dist:
            min_distance = dist

    return min_distance


def plot_distance_from_mars_vs_time(path):
    print(path)
    lines = []
    with open(path, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]

    launching_time = int(lines[0])

    offset = 1 + launching_time * 4

    mars_index = 3
    spaceship_index = 4

    step = 1000 * 100

    times = np.arange(launching_time*step, 10000000 * 100, step)
    distances = []

    for index in range(int((len(lines)-offset) / 5)):
        temp_values = lines[index*5+offset:index*5 + 5 + offset]
        spaceship_x, spaceship_y = temp_values[spaceship_index].split()
        mars_x, mars_y = temp_values[mars_index].split()

        dist = np.linalg.norm(np.array([float(spaceship_x), float(spaceship_y)]) - np.array([float(mars_x),
                                                                                             float(mars_y)]))
        distances.append(dist)
    plt.figure()
    #plt.yscale("log")
    plt.plot(times, distances)
    plt.axhline(y=3389.5, xmin=min(times), xmax=max(times), color='r')


if __name__ == "__main__":
    main()
