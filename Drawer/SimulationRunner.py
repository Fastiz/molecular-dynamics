
import time
from Drawer import Drawer
from FilesReader import FilesReader

class SimulationRunner:

    def runSingleSimulation(self, path):
        reader = FilesReader(path + 'dynamic_file')
        mapSize = reader.readStatic(path + 'static_file')
        print(mapSize)
        drawer = Drawer(mapSize)
        positions = reader.readNextPosition()
        drawer.firstUpdate()
        time.sleep(1)

        while len(positions) > 0:
            drawer.update(positions)
            positions = reader.readNextPosition()
            skipPositions = 50
            while skipPositions > 0:
                reader.readNextPosition()
                skipPositions -= 1
