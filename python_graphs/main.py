import os

import numpy as np
import matplotlib.pyplot as plt


def main():
    #plot_numerical_vs_analytical()
    #plot_error_vs_step()
    plot_min_distance_from_mars_all_times()
    plt.show()


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

    plt4, = plt.plot(times, analytical_solution, label="Analitica")

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

    print("(%s) Error cuadratico medio: %s" % (label, mean_squared_error))

    return plt.plot(times, numerical_solution, label=label)


def plot_min_distance_from_mars_all_times():
    step = 1000 * 50
    iterations = 701
    TIME_STEP = 24 * 60 * 60
    directory = "../results/gravity/"

    times = np.arange(0, TIME_STEP * iterations, TIME_STEP)
    min_distances = []

    for filename in sorted(os.listdir(directory), key=lambda file: int(file[3:])):
        min_distances.append(min_distance_from_mars(directory + filename))

    plt.figure()
    plt.plot(times, min_distances)
    plt.hlines(y=3389.5, xmin=min(times), xmax=max(times), color="r")

    min_val = min(min_distances)
    index = min_distances.index(min_val)
    print(min_distances[index])
    print(times[index])
    print("Min value: "+str(min_val))
    print("For animation use file: sim"+str(int(times[index]/TIME_STEP)))
    plot_distance_from_mars_vs_time("../results/gravity/sim"+str(int(times[index]/TIME_STEP)))

plt.show()


def min_distance_from_mars(path):
    lines = []
    with open(path, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]

    launching_time = int(lines[0])

    offset = 1 + launching_time * 8

    min_distance = -1

    mars_index = 3
    spaceship_index = 8

    for index in range(int((len(lines)-offset) / 9)):
        temp_values = lines[index*9+offset:(index+1)*9 + offset]
        spaceship_x, spaceship_y, _, _ = temp_values[spaceship_index].split()
        mars_x, mars_y, _, _ = temp_values[mars_index].split()

        dist = np.linalg.norm(np.array([float(spaceship_x), float(spaceship_y)]) - np.array([float(mars_x),
                                                                                             float(mars_y)]))

        if min_distance == -1 or min_distance > dist:
            min_distance = dist

    return min_distance


def plot_distance_from_mars_vs_time(path):
    lines = []
    with open(path, 'r') as f:  # open in readonly mode
        lines = [line.rstrip() for line in f]

    launching_time = int(lines[0])

    offset = 1 + launching_time * 8

    mars_index = 3
    spaceship_index = 8

    step = 1000 * 50

    distances = []

    speeds = []

    for index in range(int((len(lines)-offset) / 9)):
        temp_values = lines[index*9+offset:index*9 + 9 + offset]
        spaceship_x, spaceship_y, spaceship_vx, spaceship_vy = temp_values[spaceship_index].split()
        mars_x, mars_y, mars_vx, mars_vy = temp_values[mars_index].split()

        dist = np.linalg.norm(np.array([float(spaceship_x), float(spaceship_y)]) - np.array([float(mars_x),
                                                                                             float(mars_y)]))
        distances.append(dist)
        speeds.append(np.linalg.norm(np.array([float(spaceship_vx), float(spaceship_vy)])))

    times = np.arange(launching_time * step, (launching_time + len(distances)) * step, step)

    plt.figure()

    plt.plot(times, distances)
    plt.hlines(y=3389.5, xmin=min(times), xmax=max(times), color='r')

    plt.figure()
    plt.plot(times, speeds)
    print(speeds[distances.index(min(distances))])
    plt.vlines(x=times[distances.index(min(distances))], ymin=min(speeds), ymax=max(speeds), color='r')

if __name__ == "__main__":
    main()
