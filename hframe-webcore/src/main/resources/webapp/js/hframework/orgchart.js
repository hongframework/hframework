(function($) {
    $(function() {
        /*********************** chart editing **********************/
        //var jsonDs4 = {
        //    'name': 'Ball game',
        //    'relationship': '001',
        //    'title': 'department manager',
        //    'children': [
        //        { 'name': 'Football', 'relationship': '110', 'title': 'department manager' },
        //        { 'name': 'Basketball', 'relationship': '110', 'title': 'department manager' },
        //        { 'name': 'Volleyball', 'relationship': '110', 'title': 'department manager' }
        //    ]
        //};
        //alert(jsonDs4);
        $('#chart-edit').orgchart({
            'data' : JSON.parse($("#org-chart-data").text()),
            'direction': 'l2r',
            'draggable': true,
            'exportButton': true,
            'exportFilename': 'SportsChart',
            'parentNodeSymbol': 'fa-th-large',
            'nodeContent': 'url',
            'nodeID': 'id',
            'createNode': function($node, data) {
                $node.on('click', function(event) {
                    if (!$(event.target).is('.edge')) {
                        $('#selected-node').val(data.name).data('node', $node);
                    }
                });
            }
        }).on('click', '.orgchart', function(event) {
            if (!$(event.target).closest('.node').length) {
                $('#selected-node').val('');
            }
        });
        $('input[name="chart-state"]').on('click', function() {
            $('#chart-edit').children('.orgchart').toggleClass('view-state');
            $('#edit-panel').toggleClass('view-state');
            if ($(this).val() === 'edit') {
                $('.orgchart').find('tr').removeClass('hidden')
                    .find('td').removeClass('hidden')
                    .find('.node').removeClass('slide-up slide-down slide-right slide-left');
            } else {
                $('#btn-reset').trigger('click');
            }
        });
        $('input[name="node-type"]').on('click', function() {
            var $this = $(this);
            if ($this.val() === 'parent') {
                $('#edit-panel').addClass('edit-parent-node');
                $('#new-nodelist').children(':gt(0)').remove();
            } else {
                $('#edit-panel').removeClass('edit-parent-node');
            }
        });
        $('#btn-add-input').on('click', function() {
            $('#new-nodelist').append('<li><input type="text" class="new-node"></li>');
        });
        $('#btn-remove-input').on('click', function() {
            var inputs = $('#new-nodelist').children('li');
            if (inputs.length > 1) {
                inputs.last().remove();
            }
        });
        $('#btn-add-nodes').on('click', function() {
            var $chartEdit = $('#chart-edit');
            var nodeVals = [];
            $('#new-nodelist').find('.new-node').each(function(index, item) {
                var validVal = item.value.trim();
                if (validVal.length) {
                    nodeVals.push(validVal);
                }
            });
            var $node = $('#selected-node').data('node');
            if (!nodeVals.length) {
                alert('Please input value for new node');
                return;
            }
            var nodeType = $('input[name="node-type"]:checked');
            if (nodeType.val() !== 'parent' && !$node) {
                alert('Please select one node in orgchart');
                return;
            }
            if (!nodeType.length) {
                alert('Please select a node type');
                return;
            }
            if (nodeType.val() === 'parent') {
                $chartEdit.orgchart('addParent', $chartEdit.find('.node:first'), { 'name': nodeVals[0] });
            } else if (nodeType.val() === 'siblings') {
                $chartEdit.orgchart('addSiblings', $node,
                    { 'siblings': nodeVals.map(function(item) { return { 'name': item, 'relationship': '110' }; })
                    });
            } else {
                var hasChild = $node.parent().attr('colspan') > 0 ? true : false;
                if (!hasChild) {
                    var rel = nodeVals.length > 1 ? '110' : '100';
                    $chartEdit.orgchart('addChildren', $node, {
                        'children': nodeVals.map(function(item) {
                            return { 'name': item, 'relationship': rel };
                        })
                    }, $.extend({}, $chartEdit.find('.orgchart').data('options'), { depth: 0 }));
                } else {
                    $chartEdit.orgchart('addSiblings', $node.closest('tr').siblings('.nodes').find('.node:first'),
                        { 'siblings': nodeVals.map(function(item) { return { 'name': item, 'relationship': '110' }; })
                        });
                }
            }
        });
        $('#btn-delete-nodes').on('click', function() {
            var $node = $('#selected-node').data('node');
            if (!$node) {
                alert('Please select one node in orgchart');
                return;
            }
            $('#chart-edit').orgchart('removeNodes', $node);
            $('#selected-node').data('node', null);
        });
        $('#btn-reset').on('click', function() {
            $('#chart-edit').children('.orgchart').trigger('click');
            $('#new-nodelist').find('input:first').val('').parent().siblings().remove();
            $('#node-type-panel').find('input').prop('checked', false);
        });

        $('#chart-submit').on('click', function() {
            var hierarchy = $('#chart-edit').orgchart('getHierarchy');

            var _data = JSON.stringify(hierarchy, null, 2);
            alert(_data);
            $.ajax({
                url: "/" + url,
                data: _data,
                type: 'post',
                contentType : 'application/json;charset=utf-8',
                dataType: 'json',
                success: function(data){
                    if(data.resultCode != '0') {
                        alert(data.resultMessage);
                        return;
                    }

                    delete $action[$type];
                    doEvent($action,$param,$this);
                }
            });

            //if (!$('pre').length) {
            //    var hierarchy = $('#get-hierarchy').orgchart('getHierarchy');
            //    $('#btn-export-hier').after('<pre>').next().append(JSON.stringify(hierarchy, null, 2));
            //}
        });
    });
})(jQuery);

