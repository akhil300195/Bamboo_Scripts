
#! /Library/Frameworks/Python.framework/Versions/3.7/bin/python3

from sys import argv
from bs4 import BeautifulSoup


def sample_specs_into_file(html_file):
    """ Okay """

    with open(html_file) as fp:
        soup = BeautifulSoup(fp, features="html.parser")
        print(soup)
        print(soup.textarea.text)

    with open("PlanSpec.java", 'w') as ps:
        ps.write(soup.textarea.text)


if __name__ == "__main__":
    """ Invoke the function with the log file as parameter """

    log_file = argv[1]
    sample_specs_into_file(log_file)