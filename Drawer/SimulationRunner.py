import time

import pygame

from Drawer import Drawer
from FilesReader import FilesReader
from Particle import Particle


class SimulationRunner:

    COLORS = [(252, 198, 3), (3, 84, 145), (252, 90, 3), (209, 135, 17), (173, 154, 75), (167, 185, 199), (255, 255, 255), (255, 255, 255)]
    RADIUS = [20, 10, 5, 2, 16, 4, 0, 1]

    def __init__(self, path):
        self.reader = FilesReader(path + 'dynamic_file', path + 'static_file')
        self.drawer = None

        self.run_single_simulation()

    def read_positions(self):
        positions = self.reader.read_next_position()
        if positions is None:
            return None

        particles = []
        for i in range(len(positions)):
            particles.append(Particle(positions[i], self.RADIUS[i], self.COLORS[i]))

        return particles

    def run_single_simulation(self):
        # Initialise screen
        pygame.init()
        screen = pygame.display.set_mode((0, 0), pygame.FULLSCREEN)
        #screen = pygame.display.set_mode((500, 500))

        # Fill background
        background = pygame.Surface(screen.get_size())
        background = background.convert()
        background.fill((0, 0, 0))

        # Display some text
        font = pygame.font.Font(None, 30)
        text = font.render("ESC to exit", 1, (255, 255, 255))
        textpos = text.get_rect()
        textpos.centerx = 60
        textpos.centery = 10
        background.blit(text, textpos)

        # Blit everything to the screen
        screen.blit(background, (0, 0))
        pygame.display.flip()

        drawer = Drawer(screen, self.reader.dimensions)

        flag = True
        running = True
        while running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False
                if event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_ESCAPE:
                        running = False
            particles = self.read_positions()

            if particles is None:
                flag = False

            screen.blit(background, (0, 0))

            if flag:

                drawer.update(particles)
                pygame.display.flip()