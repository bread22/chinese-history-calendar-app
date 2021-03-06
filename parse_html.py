# -*- coding: utf-8 -*-
from time import sleep
import re
from bs4 import BeautifulSoup
import os
import sqlite3

HTML_PATH = "cal_html"
DOW = ["日", "一", "二", "三", "四", "五", "六", "七"]
TIAN_GAN = ("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
DI_ZHI = ("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
DATABASE = "calendar.db"
DB_TABLE = "calendar"


def next_ganzhi(current_ganzhi):
    tian_gan, di_zhi = tuple(current_ganzhi)
    next_tian_gan = TIAN_GAN[(TIAN_GAN.index(tian_gan) + 1) % len(TIAN_GAN)]
    next_di_zhi = DI_ZHI[(DI_ZHI.index(di_zhi) + 1) % len(DI_ZHI)]
    return f"{next_tian_gan}{next_di_zhi}"


def parse_single_html(f_name):
    dynasty, emperor, nianhao = os.path.splitext(f_name)[0].split("_")[1:]
    years = "".join([dynasty, emperor, nianhao])
    print(years)

    # Load single HTML file to bs4
    with open(os.path.join(HTML_PATH, f_name), encoding="utf-8") as fp:
        soup = BeautifulSoup(fp, "lxml")

    root = soup.html.body
    current_tag = root.find_next()

    c_day = 0
    w_month = c_month = ganzhi_day = None
    all_days = []
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
                    not current_tag
                    or current_tag.name == "td"
                    and current_tag.attrs.get("class") == ["y"]
                ):
                    # 新的一年或者EOF
                    break
                # 新的一月
                if current_tag.name == "th" and current_tag.attrs.get("class") == ["d"]:
                    c_month = current_tag.text
                    c_day = 0
                # 干支
                if current_tag.name == "td" and current_tag.attrs.get("class") == [
                    "cz"
                ]:
                    ganzhi_day = current_tag.text
                    current_tag = current_tag.find_next()
                # 日期，非干支
                if current_tag.name == "td" and current_tag.attrs.get("class") != [
                    "cz"
                ]:
                    if "-" == current_tag.text:
                        continue
                    if "/" in current_tag.text:
                        w_month, w_day = current_tag.text.split("/")
                        if "1/1" == current_tag.text:
                            w_year += 1
                    else:
                        w_day = current_tag.text
                    c_day += 1
                    if c_month == "1" and c_day == 1:
                        print(
                            f"{years}-{c_year}年-({ganzhi_year})-{c_month}月-{c_day}({ganzhi_day})-公元{w_year}-{w_month}-{w_day}-星期{dow}"
                        )
                    all_days.append(
                        (
                            dynasty,
                            emperor,
                            nianhao,
                            c_year,
                            ganzhi_year,
                            c_month,
                            c_day,
                            ganzhi_day,
                            w_year,
                            w_month,
                            w_day,
                            dow,
                        )
                    )
                    # sleep(0.01)
                    ganzhi_day = next_ganzhi(ganzhi_day)
                    dow = (dow + 1) % 7

        else:
            current_tag = current_tag.find_next()

    return all_days


all_files = os.listdir(HTML_PATH)
all_days = []

for file_name in all_files:
    days = parse_single_html(file_name)
    all_days.extend(days)

db_conn = sqlite3.connect(DATABASE)
db_cursor = db_conn.cursor()
db_cursor.executemany("INSERT INTO calendar VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", all_days)
db_conn.commit()
db_conn.close()
