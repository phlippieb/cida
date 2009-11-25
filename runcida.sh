#!/bin/bash
#
# Copyright (C) 2009
# Computational Intelligence Research Group (CIRG@UP)
# Department of Computer Science
# University of Pretoria
# South Africa
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#

if [ -f target/cida-0.1.jar ]; then
    nice java -server -Xms1000M -Xmx2000M -jar target/cida-0.1.jar $@
else
    if [ -f cida-0.1.jar ]; then
        nice java -server -Xms1000M -Xmx2000M -jar cida-0.1.jar $@
    else
        echo "CIDA jar not found."
    fi
fi

