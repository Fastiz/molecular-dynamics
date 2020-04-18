

class Particle:
    def __init__(self, pos, radius, color):
        self.pos = pos
        self.radius = radius
        self.color = color

    def get_pos(self):
        return self.pos

    def get_radius(self):
        return self.radius

    def set_pos(self, pos):
        self.pos = pos

    def get_color(self):
        return self.color
