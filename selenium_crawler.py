# -*- coding: utf-8 -*-
"""把二十史朔闰表从 https://sinocal.sinica.edu.tw/ 按照 朝代-皇帝-年号 下载至html以待后续处理"""
import random
from os.path import join
from time import sleep

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select

BASE_URL = "https://sinocal.sinica.edu.tw/"
HTML_PATH = "cal_html"

browser = webdriver.Chrome()
browser.get(BASE_URL)
browser.set_window_size(1026, 1112)
browser.find_element(By.NAME, "all").click()

html_index = 0
browser.find_element(By.ID, "dyna").click()
dropdown_dynasty = browser.find_element(By.ID, "dyna")
dropdown_options = Select(dropdown_dynasty)
dynasties = []
for dynasty in dropdown_options.options:
    if dynasty.text:
        dynasties.append(dynasty.text)
for dynasty in dynasties:
    print(f"朝代：{dynasty}")
    sleep(random.uniform(1, 3))
    browser.find_element(By.ID, "dyna").click()
    dropdown_dynasty = browser.find_element(By.ID, "dyna")
    dropdown_dynasty.find_element(By.XPATH, "//option[. = '{0}']".format(dynasty)).click()
    browser.find_element(By.ID, "king").click()
    dropdown_emperor = browser.find_element(By.ID, "king")
    dropdown_options = Select(dropdown_emperor)
    emperors = []
    for emperor in dropdown_options.options:
        if emperor.text:
            emperors.append(emperor.text)
    for emperor in emperors:
        print(f"皇帝：{emperor}")
        browser.find_element(By.ID, "king").click()
        dropdown_emperor = browser.find_element(By.ID, "king")
        dropdown_emperor.find_element(By.XPATH, "//option[. = '{0}']".format(emperor)).click()
        browser.find_element(By.ID, "reign").click()
        dropdown_nianhao = browser.find_element(By.ID, "reign")
        dropdown_options = Select(dropdown_nianhao)
        nianhaos = []
        for nianhao in dropdown_options.options:
            if nianhao.text and "*" not in nianhao.text:
                nianhaos.append(nianhao.text)
        print(f"年号：{nianhaos}")
        for nianhao in nianhaos:
            sleep(random.uniform(1, 3))
            browser.find_element(By.ID, "reign").click()
            dropdown_nianhao = browser.find_element(By.ID, "reign")
            dropdown_nianhao.find_element(By.XPATH, "//option[. = '{0}']".format(nianhao)).click()
            sleep(random.uniform(1, 3))
            browser.find_element(By.CSS_SELECTOR, ".act:nth-child(2)").click()
            browser.switch_to.frame('resultFrame')
            html_index += 1
            result_html = f"{html_index:04}_{dynasty}_{emperor}_{nianhao}.html"
            with open(join(HTML_PATH, result_html), mode='w', encoding="utf-8") as fp:
                fp.write(browser.page_source)
            browser.switch_to.default_content()
