
class FilesReader:
    def __init__(self, dynamic_path, static_path):
        self.dynamicLines = open(dynamic_path, "r").read().splitlines()
        self.dynamicLineIndex = 1
        self.particles_radius = None
        self.particles_color = None
        self.dimensions = None

        self.read_static(static_path)

    def read_map_size(self, line):
        array = line.split()
        self.dimensions = (float(array[0]), float(array[1]))

    def read_next_position(self):
        lines = self.dynamicLines
        positions = []

        while self.dynamicLineIndex < len(lines)-1 and lines[self.dynamicLineIndex][0] != '#':
            line = lines[self.dynamicLineIndex]
            x, y = line.split(" ")
            positions.append((float(x), float(y)))
            self.dynamicLineIndex += 1

        if not self.dynamicLineIndex < len(lines):
            return None

        self.dynamicLineIndex += 1

        return positions

    def read_static(self, path):
        static_file = open(path, "r")

        lines = static_file.read().splitlines()

        self.read_map_size(lines[0])

        self.particles_radius = []

        for line in lines[1:]:
            self.particles_radius.append(float(line))

    def get_particles_radius(self):
        return self.particles_radius

    def get_dimensions(self):
        return self.dimensions
