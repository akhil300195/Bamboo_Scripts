from sys import argv
import datetime



def compare(year, month, ddate, last_year_date):
    """ Compares the dates passed to this module and returns the result """

    return datetime.date(int(year), int(month), int(ddate)) < datetime.datetime.strptime(last_year_date, "%d%m%Y").date()


if __name__ == "__main__":

    year = argv[1]
    month = argv[2]
    ddate = argv[3]
    last_year_date = argv[4]
    print(compare(int(year), int(month), int(ddate), last_year_date))
