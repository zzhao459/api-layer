#
# This program and the accompanying materials are made available under the terms of the
# Eclipse Public License v2.0 which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-v20.html
#
# SPDX-License-Identifier: EPL-2.0
#
# Copyright Contributors to the Zowe Project.
#

zowe jobs cancel job $(zowe jobs list js | grep JZOWEXXX.*ACTIVE | sed -n -e 's/ JZOWEXXX.*$//p')
zowe-api-dev start --job

