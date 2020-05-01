import pygame

import pygame.gfxdraw


class Drawer:
    BORDER_COLOR = (255, 255, 255)
    BORDER_WIDTH = 3

    MARGIN = 5

    def __init__(self, screen, dimensions):
        self.resolution = pygame.display.get_surface().get_size()
        self.screen = screen
        self.realDimensions = dimensions

        map_x, map_y = dimensions
        res_x, res_y = pygame.display.get_surface().get_size()
        self.SCREEN_CENTER = (int(res_x/2), int(res_y/2))
        if map_x > map_y:
            self.X_SIZE = res_x - 2 * self.MARGIN
            self.Y_SIZE = self.X_SIZE * (map_y / map_x)
            self.X_OFFSET = self.MARGIN
            self.Y_OFFSET = (res_y - self.Y_SIZE)/2
        else:
            self.Y_SIZE = res_y - 2 * self.MARGIN
            self.X_SIZE = self.Y_SIZE * (map_x / map_y)
            self.Y_OFFSET = self.MARGIN
            self.X_OFFSET = (res_x - self.X_SIZE)/2

    def normalize_distance_magnitude(self, magnitude):
        return magnitude * max(self.X_SIZE, self.Y_SIZE) / max(self.realDimensions[0], self.realDimensions[1])

    def draw_particles(self, particles):
        for particle in particles:
            x, y = particle.get_pos()
            pygame.draw.circle(self.screen, particle.get_color(), (int(self.normalize_distance_magnitude(x) + self.X_OFFSET),
                                                                   int(self.normalize_distance_magnitude(
                                                                       y) + self.Y_OFFSET)), int(self.normalize_distance_magnitude(
                particle.get_radius()))
            )

    def draw_walls(self):
        pygame.draw.rect(self.screen, self.BORDER_COLOR, pygame.Rect(self.X_OFFSET, self.Y_OFFSET, self.X_SIZE,
                                                                     self.Y_SIZE), self.BORDER_WIDTH)

    def draw_wall(self):
        pygame.draw.rect(self.screen, self.BORDER_COLOR, pygame.Rect(self.X_OFFSET, self.Y_OFFSET + self.Y_SIZE/2,
                                                                     self.X_SIZE/2 -
                                                                     self.normalize_distance_magnitude(1.5/2.0),
                                                                     self.BORDER_WIDTH), 0)
        pygame.draw.rect(self.screen, self.BORDER_COLOR, pygame.Rect(self.X_OFFSET + self.X_SIZE/2 +
                                                                     self.normalize_distance_magnitude(1.5/2.0), self.Y_OFFSET + self.Y_SIZE/2,
                                                                     self.X_SIZE/2.0 - self.normalize_distance_magnitude(1.5/2.0),
                                                                     self.BORDER_WIDTH), 0)

    def update(self, particles):
        self.draw_particles(particles)
        self.draw_walls()
        self.draw_wall()
        pygame.display.update()
