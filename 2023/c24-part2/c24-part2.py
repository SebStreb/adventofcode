import z3

if __name__ == '__main__':
    file = open("inputs/c24", "r")
    lines = file.readlines()

    stones = []
    for line in lines:
        startStr, velocityStr = line.split(" @ ")
        startX, startY, startZ = startStr.split(", ")
        velocityX, velocityY, velocityZ = velocityStr.split(", ")
        stones.append(((int(startX), int(startY), int(startZ)), (int(velocityX), int(velocityY), int(velocityZ))))

    solver = z3.Solver()
    x, y, z, vx, vy, vz = [z3.Int(var) for var in ["x", "y", "z", "vx", "vy", "vz"]]

    i = 0
    for start, velocity in stones:
        (startX, startY, startZ) = start
        (velocityX, velocityY, velocityZ) = velocity

        i += 1
        tI = z3.Int(f"t{i}")

        solver.add(tI >= 0)
        solver.add(x + vx * tI == startX + velocityX * tI)
        solver.add(y + vy * tI == startY + velocityY * tI)
        solver.add(z + vz * tI == startZ + velocityZ * tI)

    if solver.check() == z3.sat:
        model = solver.model()
        (x, y, z) = (model.eval(x), model.eval(y), model.eval(z))
        solution = x.as_long() + y.as_long() + z.as_long()
        print(solution)
