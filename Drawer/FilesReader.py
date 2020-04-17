import sys

class FilesReader:

    dynamicFile = None
    staticFile = None

    height = 0
    width = 0
    particles = 0
    iterations = 0
    chunkSize = 0
    map = None

    dynamicLineNumber = 0
    dynamicLines = None

    maxVel = 0

    def __init__(self, path):
        self.dynamicFile = open(path, "r")
        self.dynamicLines = self.dynamicFile.readlines()

    def readMapSize(self, line):
        array = line.split()
        self.height = array[0]
        self.width = array[1]

    def readPosition(self, line):
        pos = [0, 0]
        i = 0
        for num in line.split():
            pos[i] = float(num) / 5000000
            i += 1
        return pos


    def readNextPosition(self):
        lines = self.dynamicLines

        if len(lines) > self.dynamicLineNumber:
            line = lines[self.dynamicLineNumber]

            while line[0] == '#':
                self.dynamicLineNumber += 1
                line = lines[self.dynamicLineNumber]

            endNumber = self.dynamicLineNumber
            while line[0] != '#' and endNumber < len(lines) - 1:
                endNumber += 1
                line = lines[endNumber]

            positions = [self.readPosition(line) for line in lines[self.dynamicLineNumber:endNumber]]

            self.dynamicLineNumber = endNumber
            return positions

    def readStatic(self, path):
        self.staticFile = open(path, "r")
        lines = self.staticFile.readlines()
        line = lines[0]
        mapSize = float(line.split()[0]) / 5000000
        return mapSize
