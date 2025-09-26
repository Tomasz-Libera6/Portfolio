import os
import time
import threading
import tkinter
import numpy
from tkinter import *
from tkinter.ttk import *
from algorithms import *

clock = pygame.time.Clock()


def timer():
    global iterationTime
    iterationTime = 0
    while True:
        if solveButton["text"] == "Stop" or generateButton["text"] == "Stop":
            timeLabel.config(text="Time: " + str(iterationTime // 60) + ":" + str(iterationTime % 60))
            time.sleep(1)
            iterationTime += 1

            while pauseButton["text"] == "Continue":
                Tk.update(mainWindow)

        elif solveButton["text"] == "Solve" and generateButton["text"] == "Generate":
            iterationTime = 0


def generate():
    global horizontal, vertical, gridCells, breakValue, endCell
    canSolve = 0

    if generateButton["text"] == "Generate":
        if int(horizontalSpin.get()) > 50:
            horizontalSpin.set(50)
        elif int(horizontalSpin.get()) < 2:
            horizontalSpin.set(2)
        if int(verticalSpin.get()) > 50:
            verticalSpin.set(50)
        elif int(verticalSpin.get()) < 2:
            verticalSpin.set(2)
        generateButton.config(text="Stop")
        pauseButton.config(state="normal")
        solveButton.config(state="disabled")
        resetButton.config(state="disabled")
        modelComboBox.config(state="disabled")
        iterationInput.config(state="disabled")
        horizontalSpin.config(state="disabled")
        verticalSpin.config(state="disabled")
        attemptInput.config(state="disabled")

        horizontal = 20 * int(horizontalSpin.get())
        vertical = 20 * int(verticalSpin.get())
        pygame.display.set_mode((horizontal + 1, vertical + 1))
        endCell = int(int(horizontalSpin.get()) * int(verticalSpin.get()) - 1)

        gridCells = [Cell(column, row) for row in range(int(verticalSpin.get()))
                     for column in range(int(horizontalSpin.get()))]
        currentCell = gridCells[endCell]
        stack = []
        breakValue = 1

        while breakValue < len(gridCells) and generateButton["text"] == "Stop":
            screen.fill("grey")
            [cell.drawCell(screen) for cell in gridCells]
            currentCell.created = True
            currentCell.drawCurrentCell(screen)
            nextCell = currentCell.neighborsCreate(rows=int(verticalSpin.get()), columns=int(horizontalSpin.get()),
                                                   gridCells=gridCells)
            if nextCell:
                nextCell.created = True
                breakValue += 1
                stack.append(currentCell)
                removeWalls(currentCell, nextCell)
                currentCell = nextCell
            elif stack:
                currentCell = stack.pop()

            while pauseButton["text"] == "Continue":
                Tk.update(mainWindow)

            pygame.display.flip()
            Tk.update(mainWindow)
            clock.tick(0)

        [cell.drawCell(screen) for cell in gridCells]
        pygame.display.flip()

        if breakValue == len(gridCells):
            canSolve = 1

    if canSolve == 1:
        solveButton.config(state="normal")

    generateButton.config(text="Generate")
    pauseButton.config(text="Pause")
    pauseButton.config(state="disabled")
    resetButton.config(state="normal")
    modelComboBox.config(state="normal")
    iterationInput.config(state="normal")
    horizontalSpin.config(state="normal")
    verticalSpin.config(state="normal")
    attemptInput.config(state="normal")


def solve():
    global Steps, fileQLearning, fileQLearningTrace, fileSarsa, fileSarsaTrace
    if solveButton["text"] == "Solve":
        if int(horizontalSpin.get()) > 50:
            horizontalSpin.set(50)
        elif int(horizontalSpin.get()) < 2:
            horizontalSpin.set(2)
        if int(verticalSpin.get()) > 100:
            verticalSpin.set(100)
        elif int(verticalSpin.get()) < 2:
            verticalSpin.set(2)

        solveButton.config(text="Stop")
        generateButton.config(state="disabled")
        pauseButton.config(state="normal")
        resetButton.config(state="disabled")
        iterationInput.config(state="disabled")
        modelComboBox.config(state="disabled")
        horizontalSpin.config(state="disabled")
        verticalSpin.config(state="disabled")
        attemptInput.config(state="disabled")
        timeLabel.config(text="Time:")
        iterationCountLabel.config(text="Iteration:")
        attemptCountLabel.config(text="Attempt:")
        StepsLabel.config(text="Steps:")

        fileRecord = str(attemptInput.get()) + " " + str(horizontalSpin.get()) + "x" + str(verticalSpin.get()) + "\n"
        currentModel = modelComboBox.current()
        if currentModel == 0:
            fileQLearning = open('QLearning.txt', 'w')
            fileQLearning.write(fileRecord)
        elif currentModel == 1:
            fileQLearningTrace = open('QLearningTrace.txt', 'w')
            fileQLearningTrace.write(fileRecord)
        elif currentModel == 2:
            fileSarsa = open('Sarsa.txt', 'w')
            fileSarsa.write(fileRecord)
        elif currentModel == 3:
            fileSarsaTrace = open('SarsaTrace.txt', 'w')
            fileSarsaTrace.write(fileRecord)

        penaltyWall = -100
        eligibilityDecay = 0.66
        alpha = 0.7
        gamma = 0.9
        attemptTable = numpy.zeros((int(attemptInput.get()), int(iterationInput.get())))
        iterationTable = numpy.zeros(int(attemptInput.get()))
        timeTable = numpy.zeros(int(attemptInput.get()))
        averageAttemptTable = numpy.zeros(int(iterationInput.get()))
        averageTime = 0
        averageIteration = 0
        minSteps = 0

        for i in range(0, int(attemptInput.get())):
            drawPath = False
            dictionary = dict()
            eligibilityTrace = dict()
            previousCell = gridCells[0]
            previousDirection = None
            attemptCountLabel.config(text="Attempt: " + str(i + 1))

            for j in range(0, int(iterationInput.get())):
                if j != 0 and solveButton["text"] == "Solve":
                    break

                currentCell = gridCells[0]
                iterationCountLabel.config(text="Iteration: " + str(j + 1))
                Steps = 0
                if j == (int(iterationInput.get()) - 1) or (attemptTable[i][j - 1] == attemptTable[i][j - 2] and
                                                            attemptTable[i][j - 1] == attemptTable[i][j - 3] and
                                                            attemptTable[i][j - 1] == attemptTable[i][j - 4] and
                                                            j >= 4):
                    previousCell = currentCell
                    drawPath = True
                    minSteps = float("{:.1f}".format(attemptTable[i][j-1]))

                if currentModel == 0:
                    while currentCell != gridCells[endCell] and solveButton["text"] == "Stop":
                        currentCell.visited = True

                        history = numpy.array([dictionary.get((currentCell, move), 0.0)
                                               for move in range(4)])
                        bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                        direction = random.choice(bestPrediction)
                        nextCell = currentCell.cellPosition(rows=int(verticalSpin.get()),
                                                            columns=int(horizontalSpin.get()),
                                                            gridCells=gridCells, direction=direction)

                        reward = countReward(cell=nextCell, endCell=gridCells[endCell])

                        if (currentCell, direction) not in dictionary.keys():
                            dictionary[(currentCell, direction)] = 0.0

                        if reward != penaltyWall:
                            if drawPath:
                                currentCell.drawPath(screen, direction)
                            Steps += 1
                            maxNext = max([dictionary.get((nextCell, move), 0.0) for move in range(4)])
                            dictionary[(currentCell, direction)] += alpha * (reward + gamma * maxNext -
                                                                             dictionary[(currentCell, direction)])
                            previousCell = currentCell
                            currentCell = nextCell
                        elif reward == penaltyWall:
                            dictionary[(currentCell, direction)] = reward
                            currentCell = previousCell

                        if not drawPath:
                            [cell.drawCell(screen) for cell in gridCells]
                            currentCell.drawCurrentCell(screen)
                        pygame.draw.rect(screen, pygame.Color("red"), (horizontal - 17, vertical - 17, 16, 16))

                        while pauseButton["text"] == "Continue":
                            Tk.update(mainWindow)

                        StepsLabel.config(text="Steps: " + str(Steps))
                        pygame.display.flip()
                        Tk.update(mainWindow)
                        clock.tick(0)

                    attemptTable[i][j] = Steps
                    if drawPath:
                        timeTable[i] = iterationTime
                        iterationTable[i] = j+1
                        break
                elif currentModel == 1:
                    while currentCell != gridCells[endCell] and solveButton["text"] == "Stop":
                        currentCell.visited = True

                        history = numpy.array([dictionary.get((currentCell, move), 0.0)
                                               for move in range(4)])
                        bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                        direction = random.choice(bestPrediction)
                        nextCell = currentCell.cellPosition(rows=int(verticalSpin.get()),
                                                            columns=int(horizontalSpin.get()),
                                                            gridCells=gridCells, direction=direction)

                        try:
                            eligibilityTrace[(currentCell, direction)] += 1
                        except KeyError:
                            eligibilityTrace[(currentCell, direction)] = 1

                        reward = countReward(cell=nextCell, endCell=gridCells[endCell])

                        if (currentCell, direction) not in dictionary.keys():
                            dictionary[(currentCell, direction)] = 0.0

                        if reward != penaltyWall:
                            if drawPath:
                                currentCell.drawPath(screen, direction)
                            Steps += 1
                            maxNext = max([dictionary.get((nextCell, move), 0.0) for move in range(4)])
                            delta = alpha * (reward + gamma * maxNext - dictionary[(currentCell, direction)])
                            for key in eligibilityTrace.keys():
                                dictionary[key] += delta * eligibilityTrace[key]
                            for key in eligibilityTrace.keys():
                                eligibilityTrace[key] *= eligibilityDecay
                            previousCell = currentCell
                            currentCell = nextCell
                        elif reward == penaltyWall:
                            dictionary[(currentCell, direction)] = reward
                            eligibilityTrace[(currentCell, direction)] = reward
                            currentCell = previousCell

                        if not drawPath:
                            [cell.drawCell(screen) for cell in gridCells]
                            currentCell.drawCurrentCell(screen)
                        pygame.draw.rect(screen, pygame.Color("red"), (horizontal - 17, vertical - 17, 16, 16))

                        while pauseButton["text"] == "Continue":
                            Tk.update(mainWindow)

                        StepsLabel.config(text="Steps: " + str(Steps))
                        pygame.display.flip()
                        Tk.update(mainWindow)
                        clock.tick(00)

                    attemptTable[i][j] = Steps
                    if drawPath:
                        timeTable[i] = iterationTime
                        iterationTable[i] = j+1
                        break
                elif currentModel == 2:
                    history = numpy.array([dictionary.get((currentCell, move), 0.0) for move in range(4)])
                    bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                    direction = random.choice(bestPrediction)
                    nextCell = currentCell.cellPosition(rows=int(verticalSpin.get()),
                                                        columns=int(horizontalSpin.get()),
                                                        gridCells=gridCells, direction=direction)

                    beginning = True
                    while currentCell != gridCells[endCell] and solveButton["text"] == "Stop":
                        currentCell.visited = True

                        if beginning:
                            beginning = False
                        else:
                            nextCell = currentCell.cellPosition(rows=int(verticalSpin.get()),
                                                                columns=int(horizontalSpin.get()),
                                                                gridCells=gridCells, direction=direction)

                        history = numpy.array([dictionary.get((nextCell, move), 0.0) for move in range(4)])
                        bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                        nextDirection = random.choice(bestPrediction)

                        reward = countReward(cell=nextCell, endCell=gridCells[endCell])

                        if (currentCell, direction) not in dictionary.keys():
                            dictionary[(currentCell, direction)] = 0.0

                        if reward != penaltyWall:
                            if drawPath:
                                currentCell.drawPath(screen, direction)
                            Steps += 1
                            bestNext = dictionary.get((nextCell, nextDirection), 0.0)
                            dictionary[(currentCell, direction)] += alpha * (reward + gamma * bestNext -
                                                                             dictionary[(currentCell, direction)])
                            previousCell = currentCell
                            currentCell = nextCell
                            previousDirection = direction
                            direction = nextDirection

                        elif reward == penaltyWall:
                            dictionary[(currentCell, direction)] = reward
                            currentCell = previousCell
                            if not previousCell == gridCells[0]:
                                direction = previousDirection
                            else:
                                history = numpy.array([dictionary.get((currentCell, move), 0.0) for move in range(4)])
                                bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                                direction = random.choice(bestPrediction)

                        if not drawPath:
                            [cell.drawCell(screen) for cell in gridCells]
                            currentCell.drawCurrentCell(screen)
                        pygame.draw.rect(screen, pygame.Color("red"), (horizontal - 17, vertical - 17, 16, 16))

                        while pauseButton["text"] == "Continue":
                            Tk.update(mainWindow)

                        StepsLabel.config(text="Steps: " + str(Steps))
                        pygame.display.flip()
                        Tk.update(mainWindow)
                        clock.tick(0)

                    attemptTable[i][j] = Steps
                    if drawPath:
                        timeTable[i] = iterationTime
                        iterationTable[i] = j+1
                        break
                elif currentModel == 3:
                    history = numpy.array([dictionary.get((currentCell, move), 0.0) for move in range(4)])
                    bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                    direction = random.choice(bestPrediction)
                    nextCell = currentCell.cellPosition(rows=int(verticalSpin.get()),
                                                        columns=int(horizontalSpin.get()),
                                                        gridCells=gridCells, direction=direction)

                    beginning = True
                    while currentCell != gridCells[endCell] and solveButton["text"] == "Stop":
                        currentCell.visited = True

                        if beginning:
                            beginning = False
                        else:
                            nextCell = currentCell.cellPosition(rows=int(verticalSpin.get()),
                                                                columns=int(horizontalSpin.get()),
                                                                gridCells=gridCells, direction=direction)

                            try:
                                eligibilityTrace[(currentCell, direction)] += 1
                            except KeyError:
                                eligibilityTrace[(currentCell, direction)] = 1

                        history = numpy.array([dictionary.get((nextCell, move), 0.0) for move in range(4)])
                        bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                        nextDirection = random.choice(bestPrediction)

                        reward = countReward(cell=nextCell, endCell=gridCells[endCell])

                        if (currentCell, direction) not in dictionary.keys():
                            dictionary[(currentCell, direction)] = 0.0

                        if reward != penaltyWall:
                            if drawPath:
                                currentCell.drawPath(screen, direction)
                            Steps += 1
                            bestNext = dictionary.get((nextCell, nextDirection), 0.0)
                            delta = alpha * (reward + gamma * bestNext - dictionary[(currentCell, direction)])

                            for key in eligibilityTrace.keys():
                                dictionary[key] += delta * eligibilityTrace[key]
                            for key in eligibilityTrace.keys():
                                eligibilityTrace[key] *= eligibilityDecay

                            previousCell = currentCell
                            currentCell = nextCell
                            previousDirection = direction
                            direction = nextDirection

                        elif reward == penaltyWall:
                            dictionary[(currentCell, direction)] = reward
                            eligibilityTrace[(currentCell, direction)] = reward
                            currentCell = previousCell
                            if not previousCell == gridCells[0]:
                                direction = previousDirection
                            else:
                                history = numpy.array([dictionary.get((currentCell, move), 0.0) for move in range(4)])
                                bestPrediction = numpy.nonzero(history == numpy.max(history))[0]
                                direction = random.choice(bestPrediction)

                        if not drawPath:
                            [cell.drawCell(screen) for cell in gridCells]
                            currentCell.drawCurrentCell(screen)
                        pygame.draw.rect(screen, pygame.Color("red"), (horizontal - 17, vertical - 17, 16, 16))

                        while pauseButton["text"] == "Continue":
                            Tk.update(mainWindow)

                        StepsLabel.config(text="Steps: " + str(Steps))
                        pygame.display.flip()
                        Tk.update(mainWindow)
                        clock.tick(0)

                    attemptTable[i][j] = Steps
                    if drawPath:
                        timeTable[i] = iterationTime
                        iterationTable[i] = j+1
                        break

        for i in range(0, int(attemptInput.get())):
            averageIteration += iterationTable[i]
            if i == 0:
                averageTime += timeTable[i]
            else:
                averageTime += timeTable[i] - timeTable[i-1]

        averageTime /= int(attemptInput.get())
        averageIteration /= int(attemptInput.get())
        fileRecord = ("{:.1f}".format(averageIteration) + "\n" + "{:.2f}".format(averageTime) + "\n")

        if currentModel == 0:
            fileQLearning.write(fileRecord)
        elif currentModel == 1:
            fileQLearningTrace.write(fileRecord)
        elif currentModel == 2:
            fileSarsa.write(fileRecord)
        elif currentModel == 3:
            fileSarsaTrace.write(fileRecord)

        for i in range(0, int(attemptInput.get())):
            for j in range(0, int(iterationInput.get())):
                averageAttemptTable[j] += attemptTable[i][j]

        for j in range(0, int(iterationInput.get())):
            averageAttemptTable[j] /= int(attemptInput.get())
            if averageAttemptTable[j] == 0:
                break
            if averageAttemptTable[j] <= minSteps:
                fileRecord = str(minSteps) + "\n"
            else:
                fileRecord = "{:.1f}".format(averageAttemptTable[j]) + "\n"
            if currentModel == 0:
                fileQLearning.write(fileRecord)
            elif currentModel == 1:
                fileQLearningTrace.write(fileRecord)
            elif currentModel == 2:
                fileSarsa.write(fileRecord)
            elif currentModel == 3:
                fileSarsaTrace.write(fileRecord)

        if currentModel == 0:
            fileQLearning.close()
        elif currentModel == 1:
            fileQLearningTrace.close()
        elif currentModel == 2:
            fileSarsa.close()
        elif currentModel == 3:
            fileSarsaTrace.close()
    solveButton.config(text="Solve")
    generateButton.config(state="normal")
    pauseButton.config(text="Pause")
    pauseButton.config(state="disabled")
    resetButton.config(state="normal")
    modelComboBox.config(state="normal")
    iterationInput.config(state="normal")
    horizontalSpin.config(state="normal")
    verticalSpin.config(state="normal")
    attemptInput.config(state="normal")


def reset():
    solveButton.config(state="disabled")
    pauseButton.config(state="disabled")
    iterationInput.delete(0, END)
    iterationInput.insert(0, 100)
    modelComboBox.current(0)
    horizontalSpin.delete(0, END)
    horizontalSpin.insert(0, 10)
    verticalSpin.delete(0, END)
    verticalSpin.insert(0, 10)
    attemptInput.delete(0, END)
    attemptInput.insert(0, 10)
    timeLabel.config(text="Time:")
    iterationCountLabel.config(text="Iteration:")
    attemptCountLabel.config(text="Attempt:")
    StepsLabel.config(text="Steps:")
    pygame.display.set_mode((10 * int(horizontalSpin.get()) + 1, 10 * int(verticalSpin.get()) + 1))
    pygame.display.flip()


def pause():
    if pauseButton["text"] == "Pause":
        pauseButton.config(text="Continue")
    elif pauseButton["text"] == "Continue":
        pauseButton.config(text="Pause")


def instructions():
    instructionWindow = Toplevel(mainWindow)
    instructionWindow.geometry("500x160")
    instructionWindow.title('Instructions')
    description = tkinter.StringVar()
    label = tkinter.Label(instructionWindow, textvariable=description, anchor="e", justify=LEFT)
    label.grid(row=1, column=1, padx=1, pady=1)
    description.set("Model: choose reinforcement learning algorithm model "
                    "\nMax Iterations: enter amount of max iterations the agent will do"
                    "\nAttempts: enter amount of times agent will learn from scratch"
                    "\nHorizontal / Vertical size: choose size of generated maze (max 50 in both sizes)"
                    "\nGenerate: generates maze based on chosen sizes"
                    "\nReset: resets all changed values to ones after starting program"
                    "\nSolve: starts chosen algorithm model to solve the maze"
                    "\nPause: pauses action and allows you to continue it"
                    "\nStop: for both Generate and Solve you can abandon action after starting it by clicking on Stop"
                    "\nLabels Iterations/Attempts/Steps/Times: display current values")


def author():
    authorWindow = Toplevel(mainWindow)
    authorWindow.geometry("180x60")
    authorWindow.title('Author')
    description = tkinter.StringVar()
    label = tkinter.Label(authorWindow, textvariable=description, anchor="e", justify=LEFT)
    label.grid(row=1, column=1, padx=1, pady=1)
    description.set("Tomasz Libera\nAdam Mickiewicz University\nPoznań, 2023")


mainWindow = Tk()
mainWindow.title("Maze settings")

os.environ["SDL_VIDEO_WINDOW_POS"] = "%d,%d" % (450, 30)
pygame.init()
screen = pygame.display.set_mode((100, 100))
pygame.display.set_caption("Maze")

widgetFrame = Frame(mainWindow)
mathFrame = Frame(mainWindow)

menuBar = Menu()
mainWindow.config(menu=menuBar)
creditsMenu = tkinter.Menu(menuBar, tearoff=0)
menuBar.add_cascade(label="Menu", menu=creditsMenu)
creditsMenu.add_command(label="Instructions", command=instructions)
creditsMenu.add_command(label="Author", command=author)
mainWindow.geometry("%dx%d+%d+%d" % (400, 160, -10, -3))

generateButton = Button(mathFrame, text="Generate", command=generate)
generateButton.grid(row=0, column=0, padx=5, pady=5)

solveButton = Button(mathFrame, text="Solve", command=solve)
solveButton.grid(row=1, column=0, padx=5, pady=5)

pauseButton = Button(mathFrame, text="Pause", command=pause)
pauseButton.grid(row=1, column=1, padx=5, pady=5)

resetButton = Button(mathFrame, text="Reset", command=reset)
resetButton.grid(row=0, column=1, padx=5, pady=5)

iterationCountLabel = Label(mathFrame, text="Iteration:")
iterationCountLabel.grid(row=2, column=0, padx=5, pady=5)

attemptCountLabel = Label(mathFrame, text="Attempt:")
attemptCountLabel.grid(row=3, column=0, padx=5, pady=5)

StepsLabel = Label(mathFrame, text="Steps: ")
StepsLabel.grid(row=2, column=1, padx=5, pady=5)

timeLabel = Label(mathFrame, text="Time:")
timeLabel.grid(row=3, column=1, padx=5, pady=5)

modelLabel = Label(widgetFrame, text="Model")
modelLabel.grid(row=0, column=0, padx=5, pady=5)
modelComboBox = Combobox(widgetFrame, state="readonly", width=15)
modelComboBox["values"] = ["QLearning", "QLearningTrace", "Sarsa", "SarsaTrace"]
modelComboBox.grid(row=0, column=1, padx=5, pady=5)

iterationInputLabel = Label(widgetFrame, text="Max Iterations")
iterationInputLabel.grid(row=1, column=0, padx=5, pady=5)
iterationInput = Entry(widgetFrame, width=15)
iterationInput.grid(row=1, column=1, padx=5, pady=5)

attemptLabel = Label(widgetFrame, text="Attempts")
attemptLabel.grid(row=2, column=0, padx=5, pady=5)
attemptInput = Entry(widgetFrame, width=15)
attemptInput.grid(row=2, column=1, padx=5, pady=5)

horizontalLabel = Label(widgetFrame, text="Horizontal size")
horizontalLabel.grid(row=3, column=0, padx=5, pady=5)
horizontalSpin = Spinbox(widgetFrame, from_=2, to=50, width=13, wrap=True)
horizontalSpin.grid(row=3, column=1, padx=5, pady=5)

verticalLabel = Label(widgetFrame, text="Vertical size")
verticalLabel.grid(row=4, column=0, padx=5, pady=5)
verticalSpin = Spinbox(widgetFrame, from_=2, to=50, width=13, wrap=True)
verticalSpin.grid(row=4, column=1, padx=5, pady=5)

widgetFrame.grid(row=0, column=0, padx=5, pady=5)
mathFrame.grid(row=0, column=1, padx=5, pady=5)

reset()

timerThread = threading.Thread(target=timer)
timerThread.setDaemon(False)
timerThread.start()

mainWindow.mainloop()
