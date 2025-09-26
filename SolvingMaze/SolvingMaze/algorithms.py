import random
import pygame


def removeWalls(currentCell, nextCell):
    differenceX = currentCell.x - nextCell.x
    if differenceX == 1:
        currentCell.walls["left"] = False
        nextCell.walls["right"] = False
    elif differenceX == -1:
        currentCell.walls["right"] = False
        nextCell.walls["left"] = False
    differenceY = currentCell.y - nextCell.y
    if differenceY == 1:
        currentCell.walls["up"] = False
        nextCell.walls["down"] = False
    elif differenceY == -1:
        currentCell.walls["down"] = False
        nextCell.walls["up"] = False


def checkCell(x, y, rows, columns, gridCells):
    index = lambda x, y: x + y * columns
    if x < 0 or x > columns - 1 or y < 0 or y > rows - 1:
        return False
    return gridCells[index(x, y)]


def countReward(cell, endCell):
    rewardExit = 100
    penaltyMove = -0.01
    penaltyVisited = -0.1
    penaltyWall = -100
    if cell == endCell:
        reward = rewardExit
    elif not cell:
        reward = penaltyWall
    elif cell.visited:
        reward = penaltyVisited
    else:
        reward = penaltyMove
    return reward


class Cell:
    def __init__(self, x, y):
        self.x, self.y = x, y
        self.walls = {"up": True, "right": True, "down": True, "left": True}
        self.created = False
        self.visited = False

    def drawCurrentCell(self, screen):
        x, y = self.x * 20, self.y * 20
        pygame.draw.rect(screen, pygame.Color("orange"), (x + 4, y + 4, 12, 12))

    def drawCell(self, screen):
        x, y = self.x * 20, self.y * 20

        if self.created:
            pygame.draw.rect(screen, pygame.Color("black"), (x, y, 20, 20))

        if self.walls["up"]:
            pygame.draw.line(screen, pygame.Color("blue"), (x, y), (x + 20, y), 2)
        if self.walls["right"]:
            pygame.draw.line(screen, pygame.Color("blue"), (x + 20, y), (x + 20, y + 20), 2)
        if self.walls["down"]:
            pygame.draw.line(screen, pygame.Color("blue"), (x + 20, y + 20), (x, y + 20), 2)
        if self.walls["left"]:
            pygame.draw.line(screen, pygame.Color("blue"), (x, y + 20), (x, y), 2)

    def neighborsCreate(self, rows, columns, gridCells):
        neighbors = []
        up = checkCell(self.x, self.y - 1, rows=rows, columns=columns, gridCells=gridCells)
        right = checkCell(self.x + 1, self.y, rows=rows, columns=columns, gridCells=gridCells)
        down = checkCell(self.x, self.y + 1, rows=rows, columns=columns, gridCells=gridCells)
        left = checkCell(self.x - 1, self.y, rows=rows, columns=columns, gridCells=gridCells)
        if up and not up.created:
            neighbors.append(up)
        if right and not right.created:
            neighbors.append(right)
        if down and not down.created:
            neighbors.append(down)
        if left and not left.created:
            neighbors.append(left)
        return random.choice(neighbors) if neighbors else False

    def neighborsMove(self, rows, columns, gridCells):
        move = []
        up = checkCell(self.x, self.y - 1, rows=rows, columns=columns, gridCells=gridCells)
        right = checkCell(self.x + 1, self.y, rows=rows, columns=columns, gridCells=gridCells)
        down = checkCell(self.x, self.y + 1, rows=rows, columns=columns, gridCells=gridCells)
        left = checkCell(self.x - 1, self.y, rows=rows, columns=columns, gridCells=gridCells)
        if up and not self.walls["up"]:
            move.append("up")
        if right and not self.walls["right"]:
            move.append("right")
        if down and not self.walls["down"]:
            move.append("down")
        if left and not self.walls["left"]:
            move.append("left")
        direction = random.choice(move)
        nextCell = self.cellPosition(rows=rows, columns=columns, gridCells=gridCells, direction=direction)
        return nextCell, direction

    def cellPosition(self, rows, columns, gridCells, direction):
        if (direction == 0 or direction == "up") and not self.walls["up"]:
            nextCell = checkCell(self.x, self.y - 1, rows=rows, columns=columns, gridCells=gridCells)
        elif (direction == 1 or direction == "right") and not self.walls["right"]:
            nextCell = checkCell(self.x + 1, self.y, rows=rows, columns=columns, gridCells=gridCells)
        elif (direction == 2 or direction == "down") and not self.walls["down"]:
            nextCell = checkCell(self.x, self.y + 1, rows=rows, columns=columns, gridCells=gridCells)
        elif (direction == 3 or direction == "left") and not self.walls["left"]:
            nextCell = checkCell(self.x - 1, self.y, rows=rows, columns=columns, gridCells=gridCells)
        else:
            nextCell = False
        return nextCell

    def drawPath(self, screen, direction):
        x, y = self.x * 20, self.y * 20
        if (direction == 0 or direction == "up") and not self.walls["up"]:
            pygame.draw.rect(screen, pygame.Color("green"), (x + 8, y - 12, 6, 26))
        elif (direction == 1 or direction == "right") and not self.walls["right"]:
            pygame.draw.rect(screen, pygame.Color("green"), (x + 8, y + 8, 26, 6))
        elif (direction == 2 or direction == "down") and not self.walls["down"]:
            pygame.draw.rect(screen, pygame.Color("green"), (x + 8, y + 8, 6, 26))
        elif (direction == 3 or direction == "left") and not self.walls["left"]:
            pygame.draw.rect(screen, pygame.Color("green"), (x - 12, y + 8, 26, 6))
