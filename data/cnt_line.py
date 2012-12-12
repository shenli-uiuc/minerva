fin = open('coor.txt', 'r')

cnt = 0
for line in fin:
    cnt += 1
    items = line.split(',')
    t = int(items[0])
    if t > 86400:
        print (line, cnt)
        break

fin.close()
