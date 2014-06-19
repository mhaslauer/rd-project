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
	
	 var rawOntology = null;
	    
    var xmlhttp;
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
    	xmlhttp=new XMLHttpRequest();
    }
    else {// code for IE6, IE5
    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    xmlhttp.onreadystatechange=function() {
    	if (xmlhttp.readyState==4 && xmlhttp.status==200) {
    		rawOntology=xmlhttp.responseText;
    		var obj = eval("(" + rawOntology + ')');
    		
    		parseOntology(obj);
    	}
    };
    
    xmlhttp.open("GET","http://localhost:8080/rd1302/ontologyexport",true);
    xmlhttp.send();
});
	

function parseOntology(rawOntology){
    $('#cy').css('width', $(window).width() - 275);
    $('#cy').css('height', $(window).height() - 100);

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

    
    // reset all buttons
    $('#send').attr("disabled", true);
    $('#confirm').attr("disabled", true);

    // set active help to step 2
    $('#step1').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step2').removeClass('help-inactive help-active').addClass('help-active');
    $('#step3').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step4').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step5').removeClass('help-inactive help-active').addClass('help-inactive');
    

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
                        
                        // set active help to step 3
                        $('#step1').removeClass('help-inactive help-active').addClass('help-inactive');
                        $('#step2').removeClass('help-inactive help-active').addClass('help-inactive');
                        $('#step3').removeClass('help-inactive help-active').addClass('help-active');
                        $('#step4').removeClass('help-inactive help-active').addClass('help-inactive');
                        $('#step5').removeClass('help-inactive help-active').addClass('help-inactive');
                    }
                    else
                    {
                        $('#send').attr('disabled', 'disabled');
                        
                        // set active help to step 2
                        $('#step1').removeClass('help-inactive help-active').addClass('help-inactive');
                        $('#step2').removeClass('help-inactive help-active').addClass('help-active');
                        $('#step3').removeClass('help-inactive help-active').addClass('help-inactive');
                        $('#step4').removeClass('help-inactive help-active').addClass('help-inactive');
                        $('#step5').removeClass('help-inactive help-active').addClass('help-inactive');
                    }
                }
            });
        }
    });
};


$(document).on('click', '#send', function() {

    // get simulation parameters and send choice to server
    var elements = window.cy.elements('.selected-sp');   
    var simParam = {
    	    parameter: []
    	};
    
    for (var i = 0; i < elements.length; i++) {
        simParam.parameter.push(
            elements[i].data().name
        );
    }
    
    // TODO send data to server and wait for result
    console.log(simParam);
    
    
    var xmlhttp;
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
    	xmlhttp=new XMLHttpRequest();
    }
    else {// code for IE6, IE5
    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    xmlhttp.onreadystatechange=function() {
    	if (xmlhttp.readyState==4 && xmlhttp.status==200) {
    		var scenarioresponse=xmlhttp.responseText;
    		var obj = eval("(" + scenarioresponse + ')');
    		
    		processScenarioResponse(obj);
    	}
    };
    
    xmlhttp.open("POST","http://localhost:8080/rd1302/analyzeparameter",true);
    xmlhttp.send(JSON.stringify(simParam));
 
});

function processScenarioResponse(scenarioresponse){
	var elements = window.cy.elements('.ss');
	
	for (var i = 0; i < elements.length; i++) {
		if (elements[i].hasClass('selected-ss')) {
			elements[i].removeClass('selected-ss');
			elements[i].addClass('unselected-ss');
		}
	}
	
	var scen = scenarioresponse.szenarios[0];
	console.log(scenarioresponse.szenarios[0]);
	
	for (var i = 0; i < elements.length; i++) {
		if(scen == elements[i].data().name){
			elements[i].addClass('selected-ss');
		}
	}
	
	$('#confirm').removeAttr('disabled');
	
    // set active help to step 4
    $('#step1').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step2').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step3').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step4').removeClass('help-inactive help-active').addClass('help-active');
    $('#step5').removeClass('help-inactive help-active').addClass('help-inactive');
}


$(document).on('click', '#confirm', function() {
	var selected_scenario = window.cy.elements('.selected-ss');
	console.log(selected_scenario);
	
	var scenario = {};
	
	scenario.szenario = selected_scenario.data().name;
	
	console.log(JSON.stringify(scenario));
	
	var xmlhttp;
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
    	xmlhttp=new XMLHttpRequest();
    }
    else {// code for IE6, IE5
    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    
    xmlhttp.onreadystatechange=function() {
    	if (xmlhttp.readyState==4 && xmlhttp.status==200) {
    		var simulationresponse = xmlhttp.responseText;
    		var obj = eval("(" + simulationresponse + ')');
    		
    		processSimulationResponse(obj);
    	}
    };
    
    xmlhttp.open("POST","http://localhost:8080/rd1302/EvaluateSimulation",true);
    xmlhttp.send(JSON.stringify(scenario));

});

function processSimulationResponse(simulationresponse){
	var elements = window.cy.elements('.sr');
	
	for (var i = 0; i < elements.length; i++) {
		if (elements[i].hasClass('selected-sr')) {
			elements[i].removeClass('selected-sr');
			elements[i].addClass('unselected-sr');
		}
	}
	
	var result = simulationresponse.simulationresult;
	console.log(simulationresponse.simulationresult);
	
	for (var i = 0; i < elements.length; i++) {
		if(result == elements[i].data().name){
			elements[i].addClass('selected-sr');
		}
	}
	
	// set active help to step 5
    $('#step1').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step2').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step3').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step4').removeClass('help-inactive help-active').addClass('help-inactive');
    $('#step5').removeClass('help-inactive help-active').addClass('help-active');
}