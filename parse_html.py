# -*- coding: utf-8 -*-
from time import sleep
import re
from bs4 import BeautifulSoup
import os

HTML_PATH = "cal_html"
DOW = ["日", "一", "二", "三", "四", "五", "六", "七"]

all_files = os.listdir(HTML_PATH)

this = all_files[223]

file_name = os.path.splitext(this)[0]
years = "".join(file_name.split("_")[1:])
print(years)

# Load single HTML file to bs4
with open(os.path.join(HTML_PATH, this), encoding="utf-8") as fp:
    soup = BeautifulSoup(fp, "lxml")

root = soup.html.body
current_tag = root.find_next()

c_day = 0
w_month = c_month = None
while current_tag:
    # 年份
    if current_tag.name == "td" and current_tag.attrs.get("class") == ["y"]:
        # 年份，干支（年），公元年份，起始星期
        c_year = re.search(f"{years}(.+?)年", current_tag.text).group(1)
        if c_year == "元":
            c_year = 1
        else:
            c_year = int(c_year)
        ganzhi_year = re.search(r"歲次：\s*?(\S+?)\s", current_tag.text).group(1)
        w_year = int(re.search(f"西元(.+?)年", current_tag.text).group(1))
        dow = re.search(r"日\((\S+?)\)起", current_tag.text).group(1)[-1]
        dow = DOW.index(dow)
        # 处理每日
        while True:
            current_tag = current_tag.find_next()
            if (
                current_tag.name == "td"
                and current_tag.attrs.get("class") == ["y"]
                or not current_tag
            ):
                # 新的一年或者EOF
                break
            # 新的一月
            if current_tag.name == "th" and current_tag.attrs.get("class") == ["d"]:
                c_month = current_tag.text
                c_day = 0
            # 日期，非干支
            if current_tag.name == "td" and current_tag.attrs.get("class") != ["cz"]:
                if "-" == current_tag.text:
                    continue
                if "/" in current_tag.text:
                    w_month, w_day = current_tag.text.split("/")
                    if "1/1" == current_tag.text:
                        w_year += 1
                else:
                    w_day = current_tag.text
                dow = (dow + 1) % 7
                c_day += 1
                print(
                    f"{years}-{c_year}年-({ganzhi_year})-{c_month}月-{c_day}-公元{w_year}-{w_month}-{w_day}-星期{dow}"
                )
                sleep(0.1)

    else:
        current_tag = current_tag.find_next()
