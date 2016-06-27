
import netCDF4
import numpy as np
from netCDF4 import ma
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages

# @BEGIN main

def main(db_pth = '.', fmodel = 'clm'):
    print "working"

    # @BEGIN Reader
    # @OUT name @AS SciName:Original
    # @OUT date @AS EventDate:Original
    # @OUT RepCon:Original

    # todo

    # @END Reader

    
    # @BEGIN SciNameValidator
    # @IN name:Original
    # @OUT name:Validated

# todo

    # @END SciNameValidator


    # @BEGIN EventDateValidator
    # @IN date @AS EventDate:Original
    # @OUT date @AS EventDate:Validated

# todo

    # @END EventDateValidator
    

    # @BEGIN FloweringTimeValidator
    # @PARAM name @AS SciName:Validated
    # @IN RepCon:Original
    # @OUT RepCon:Validated

# todo

    # @END FloweringTimeValidator

    # @BEGIN Writer
    # @IN name @AS SciName:Validated
    # @IN date @AS EventDate:Validated
    # @IN RepCon:Validated

    # todo

    # @END Writer

# @END main