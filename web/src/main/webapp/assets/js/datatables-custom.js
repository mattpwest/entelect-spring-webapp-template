/* Customize JQuery data tables. */
$(document).ready(function() {
    $.extend(true, $.fn.dataTable.defaults, {
        "dom": "<'row'<'col-sm-6'l><'col-sm-6'f>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-12'i>>" +
        "<'row'<'col-sm-12'p>>",
        "renderer": 'bootstrap',
        "pagingType": "full_numbers",
        "processing": true,
        "searchDelay": 600,
        "drawCallback": function () {
            crudConfirmDelete();
        },
        "fnServerData": function (sSource, aoData, fnCallback, oSettings) {
            //extract name/value pairs into a simpler map for use later
            var paramMap = {};
            for (var i = 0; i < aoData.length; i++) {
                paramMap[aoData[i].name] = aoData[i].value;
            }

            //page calculations
            var pageSize = paramMap.iDisplayLength;
            var start = paramMap.iDisplayStart;
            var pageNum = (typeof(start) === 'undefined') ? 0 : Math.floor(start / pageSize); // pageNum is 0 based

            //create new json structure for parameters for REST request
            var restParams = new Array();
            restParams.push({"name": "size", "value": pageSize});
            restParams.push({"name": "page", "value": pageNum});

            // extract sort information
            var sortColumnAlternatives = paramMap["sColumns"].split(",");
            for (var i = 0; i < paramMap.iColumns; i++) {
                var sortCol = paramMap["iSortCol_" + i];
                if (sortCol !== undefined) {
                    var sortDir = paramMap["sSortDir_" + i];
                    var sortName = paramMap['mDataProp_' + sortCol];
                    if (sortColumnAlternatives[sortCol] !== '') {
                        sortName = sortColumnAlternatives[sortCol];
                    }

                    restParams.push({"name": "sort", "value": sortName + "," + sortDir});
                } else {
                    break;
                }
            }

            //if we are searching by name, override the url and add the name parameter
            var url = sSource;
            if (paramMap.sSearch != '') {
                url = url + "/" + oSettings.sSearchSource;
                restParams.push({"name": "name", "value": paramMap.sSearch});
            } else if (oSettings.customData !== undefined && oSettings.customData) {
                restParams.push({"name": "name", "value": paramMap.sSearch});
            }

            //finally, make the request
            $.ajax({
                "dataType": 'json',
                "type": "GET",
                "url": url,
                "data": restParams,
                "success": function (data) {
                    if (oSettings.customData === undefined || !oSettings.customData) {
                        data.recordsTotal = data.page.totalElements;
                        data.recordsFiltered = data.page.totalElements;
                        data.data = data._embedded[oSettings.dataSource];
                    }
                    fnCallback(data);
                }
            });
        }
    });
});
