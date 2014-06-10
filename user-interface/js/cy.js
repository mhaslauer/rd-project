/**
 * Created by Stefan on 03.05.14.
 */
function getUpperCaseLettersAsLowerCase(str) {
    var s = '';
    for (var z = 0; z < str.length; z++) {
        if (str[z] === str[z].toUpperCase()) {
            s += str[z].toLowerCase();
        }
    }
    return s;
}


$(document).ready(function(){
    $('#cy').css('width', $(window).width() - 275);
    $('#cy').css('height', $(window).height() - 100);
});


$(window).resize(function() {
    $('#cy').css('width', $(window).width() - 275);
    $('#cy').css('height', $(window).height() - 100);
});


$(document).on('click', '#load', function() {
    $('#cy').css('width', $(window).width() - 275);
    $('#cy').css('height', $(window).height() - 100);



    // TODO get onotology json from server
    var rawOntology = {
        "name": "owl:Thin",
        "subclasses": [
            {
                "name": "SimulationParameter",
                "subclasses": [
                    {
                        "name": "Human",
                        "subclasses": [
                            {
                                "name": "LessHumans",
                                "subclasses": [

                                ]
                            },
                            {
                                "name": "ManyHumans",
                                "subclasses": [

                                ]
                            },
                            {
                                "name": "NormalHumans",
                                "subclasses": [

                                ]
                            },

                        ]
                    },
                    {
                        "name": "WorldSize",
                        "subclasses": [
                            {
                                "name": "BigWorld",
                                "subclasses": [

                                ]
                            },
                            {
                                "name": "NormalWorld",
                                "subclasses": [

                                ]
                            },
                            {
                                "name": "SmallWorld",
                                "subclasses": [

                                ]
                            },

                        ]
                    },
                    {
                        "name": "Zombie",
                        "subclasses": [
                            {
                                "name": "LessZombies",
                                "subclasses": [

                                ]
                            },
                            {
                                "name": "ManyZombies",
                                "subclasses": [

                                ]
                            },
                            {
                                "name": "NormalZombies",
                                "subclasses": [

                                ]
                            },

                        ]
                    },

                ]
            },
            {
                "name": "SimulationResult",
                "subclasses": [
                    {
                        "name": "LongSimulation",
                        "subclasses": [

                        ]
                    },
                    {
                        "name": "MiddleSimulation",
                        "subclasses": [

                        ]
                    },
                    {
                        "name": "ShortSimulation",
                        "subclasses": [

                        ]
                    },

                ]
            },
            {
                "name": "SimulationScenario",
                "subclasses": [
                    {
                        "name": "BestCase",
                        "subclasses": [

                        ]
                    },
                    {
                        "name": "Choice",
                        "subclasses": [

                        ]
                    },
                    {
                        "name": "HugeFight",
                        "subclasses": [

                        ]
                    },
                    {
                        "name": "WorstCase",
                        "subclasses": [

                        ]
                    },

                ]
            },

        ]
    };



    // parse ontology json
    var thing = rawOntology.subclasses;
    var nodes = [];
    var edges = [];
    var countSimParam = 0;      // count the simulation parameters for later check
    for (var i = 0; i < thing.length; i++) {
        var id1 = getUpperCaseLettersAsLowerCase(thing[i].name);
        var simulationParameters = thing[i].subclasses;

        if (id1 === 'sp') {
            nodes.push({
                data: {
                    id: id1,
                    name: thing[i].name
                },
                classes: 'sp'
            });
        }
        else if (id1 === 'ss') {
            nodes.push({
                data: {
                    id: id1,
                    name: thing[i].name
                },
                classes: 'ss'
            });
        }
        else if (id1 === 'sr') {
            nodes.push({
                data: {
                    id: id1,
                    name: thing[i].name
                },
                classes: 'sr'
            });
        }

        for (var j = 0; j < simulationParameters.length; j++) {
            var id2 = getUpperCaseLettersAsLowerCase(simulationParameters[j].name);
            var parameter = simulationParameters[j].subclasses;
            if (parameter.length === 0) {
                if (id1 === 'sr') {
                    nodes.push({
                        data: {
                            id: id1 + '.' + id2,
                            name: simulationParameters[j].name,
                            parent: id1
                        },
                        classes: 'sr'
                    });
                }
                else if (id1 === 'ss') {
                    nodes.push({
                        data: {
                            id: id1 + '.' + id2,
                            name: simulationParameters[j].name,
                            parent: id1
                        },
                        classes: 'ss'
                    });
                }
            }
            else
            {
                nodes.push({
                    data: {
                        id: id1 + '.' + id2,
                        name: simulationParameters[j].name
                    },
                    classes: 'sp'
                });
            }
            edges.push({
                data: {
                    source: id1,
                    target: id1 + '.' + id2
                }
            });

            // count every simulation parameter group for later checks
            if (id1 === 'sp') {
                countSimParam++;
            }

            for (var k = 0; k < parameter.length; k++) {
                var id3 = getUpperCaseLettersAsLowerCase(parameter[k].name);
                nodes.push({
                    data: {
                        id: id1 + '.' + id2 + '.' + id3,
                        name: parameter[k].name,
                        parent: id1 + '.' + id2
                    },
                    classes: 'sp'
                });
            }
        }
    }



    $('#cy').cytoscape({
        style: cytoscape.stylesheet()
            .selector('node')
            .css({
                'content': 'data(name)',
                'text-valign': 'center',
                'color': 'white',
                'text-outline-width': 2,
                'text-outline-color': '#777',
                'background-color': 'gray'
            })
            .selector('edge')
            .css({
                'target-arrow-shape': 'triangle'
            })

            .selector('.selected-ss')
            .css({
                'background-color': 'black'
            })
            .selector('.unselected-ss')
            .css({
                'background-color': 'blue'
            })
            .selector('.ss')
            .css({
                'background-color': 'blue'
            })

            .selector('.selected-sr')
            .css({
                'background-color': 'black'
            })
            .selector('.unselected-sr')
            .css({
                'background-color': 'green'
            })
            .selector('.sr')
            .css({
                'background-color': 'green'
            })

            .selector('.selected-sp')
            .css({
                'background-color': 'black'
            })
            .selector('.unselected-sp')
            .css({
                'background-color': 'red'
            })
            .selector('.sp')
            .css({
                'background-color': 'red'
            }),
        elements: {
            nodes: nodes,
            edges: edges
        },

        ready: function() {
            window.cy = this;

            cy.on('tap', 'node', function(e) {
                // get clicked node
                var node = e.cyTarget;

                // only child nodes can be selected for simulation (only simulation parameters)
                if (node.isChild() && node.data().id.substr(0, 3) === 'sp.') {

                    // get all other nodes in same group (of same parent at same level)
                    var siblings = node.siblings();

                    // check if another node is already selected
                    var alreadySelected = false;
                    for (var i = 0; i < siblings.size(); i++) {
                        alreadySelected = siblings[i].hasClass('selected-sp');
                        if (alreadySelected) {
                            break;
                        }
                    }

                    // if no other node is already selected in group select/unselect this one
                    if (!alreadySelected) {
                        if (node.hasClass('selected-sp')) {
                            node.removeClass('selected-sp');
                            node.addClass('unselected-sp');
                        } else {
                            node.removeClass('unselected-sp');
                            node.addClass('selected-sp');
                        }
                    }

                    // after onClick on any child node check if there are all parameters selected to enable send choice
                    if (countSimParam === cy.elements('.selected-sp').length) {
                        $('#send').removeAttr('disabled');
                    }
                    else
                    {
                        $('#send').attr('disabled', 'disabled');
                    }
                }
            });
        }
    });
});


$(document).on('click', '#send', function() {

    // get simulation parameters and send choice to server
    var elements = window.cy.elements('.selected-sp');
    var simParam = [];
    for (var i = 0; i < elements.length; i++) {
        simParam.push({
            id: elements[i].data().id,
            name: elements[i].data().name
        });
    }

    // TODO send data to server and wait for result
    console.log(simParam);

});