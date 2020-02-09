from hyperlpr_py3 import pipline as pp
import sys
#导入OpenCV库
import cv2
#读入图片

imagePath = sys.argv[1]
i = cv2.imread(imagePath)

#识别结果

image,res  = pp.SimpleRecognizePlate(i)
cv2.imwrite("processed_"+imagePath, image)
print(res)