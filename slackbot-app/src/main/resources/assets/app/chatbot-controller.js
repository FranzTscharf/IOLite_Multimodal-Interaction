(function() {
    'use strict';
    var exampleApp = angular.module('exampleApp');
    /**
     * This controller calls 'rooms' and 'devices' request handlers which are registered in the ExampleApp.java.
     */
    exampleApp.controller('chatbot-controller', [ '$scope', '$http', function($scope, $http) {
        // check if steps(bootstrap) is done!
        $scope.checkIfStepsDone = function () {
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "loadInt", /* parameters */ [ new ValueParameter("isFontEndStepsDone") ]),
                        success : function(value, storageAPI, request) {
                            document.getElementById("bootstrapSteps").style.display = "none";
                            document.getElementById("settingsCardsAndNav").style.display = "block";
                            document.getElementById("footerPage").style.display = "block";
                            console.log("steps are done!");
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            document.getElementById("bootstrapSteps").style.display = "inherit";
                            document.getElementById("settingsCardsAndNav").style.display = "none";
                            document.getElementById("settingsCardsAndNav").style.display = "none";
                            document.getElementById("footerPage").style.display = "none";
                            console.log("steps are not done yet");
                        }
                    });
                }
            });
        };
        // execute function at load
        $scope.checkIfStepsDone();
        $scope.lastStep = function () {
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "loadInt", /* parameters */ [ new ValueParameter("isFontEndStepsDone") ]),
                        success : function(value, storageAPI, request) {
                            document.getElementById("settingsCardsAndNav").style.display = "block";
                            document.getElementById("footerPage").style.display = "block";
                            console.log("steps are done!");
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            document.getElementById("bootstrapSteps").style.display = "inherit";
                            document.getElementById("settingsCardsAndNav").style.display = "none";
                            document.getElementById("settingsCardsAndNav").style.display = "none";
                            document.getElementById("footerPage").style.display = "none";
                            console.log("steps are not done yet");
                        }
                    });
                }
            });
        };
        $scope.ApiKeyValueLoadedFromStorageAPI = null;
        ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
            success : function(storageAPI) {
                storageAPI.action({
                    request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "loadString", /* parameters */ [ new ValueParameter("apikey") ]),
                    success : function(value, storageAPI, request) {
                        $scope.ApiKeyValueLoadedFromStorageAPI = value;
                        document.getElementById("inputAPIKEY").value = value;
                    },
                    error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                        console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                    }
                });
            }
        });
        $scope.ApiKeyValueSaveToStorageAPI = function() {
            //call the stoage API to save the APIKEY
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "saveString", /* parameters */ [ new ValueParameter("apikey") , new ValueParameter($scope.ApiKeyChanged)]),
                        success : function(value, storageAPI, request) {
                            console.log("ApiKeyChanged");
                            console.log($scope.ApiKeyChanged);
                            var element = document.getElementById("saveAPIKEYButton");
                            element.className = "btn btn-success";
                            element.innerHTML = "Save Changes <i class='fas fa-check'></i>";
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                        }
                    });
                }
            });
        };
        $scope.isFontEndStepsDone = function() {
            //call the stoage API to save the bootstrapDone
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "saveInt", /* parameters */ [ new ValueParameter("isFontEndStepsDone") , new ValueParameter(1)]),
                        success : function(value, storageAPI, request) {
                            console.log("isFontEndStepsDone yes it is!");
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                        }
                    });
                }
            });
        };
        $scope.ApiKeyValueSaveToStorageAPI = function() {
            //call the stoage API to save the APIKEY
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "saveString", /* parameters */ [ new ValueParameter("apikey") , new ValueParameter($scope.ApiKeyChanged)]),
                        success : function(value, storageAPI, request) {
                            console.log("ApiKeyChanged");
                            console.log($scope.ApiKeyChanged);
                            var element = document.getElementById("saveAPIKEYButton");
                            element.className = "btn btn-success";
                            element.innerHTML = "Save Changes <i class='fas fa-check'></i>";
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                        }
                    });
                }
            });
        };
        $scope.ApiKeyDialogFlowValueSaveToStorageAPI = function(akikeyDialogflow) {
            //call the stoage API to save the APIKEY
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "saveString", /* parameters */ [ new ValueParameter("apikeyDialogFlow") , new ValueParameter(akikeyDialogflow)]),
                        success : function(value, storageAPI, request) {
                            console.log("save dialogFlow key:"+akikeyDialogflow);
                            document.getElementById("inputAPIKEYDialogflow").value = akikeyDialogflow;
                            var element = document.getElementById("dialogSaveButton");
                            element.className = "btn btn-success";
                            element.innerHTML = "Save <i class='fas fa-check'></i>";
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                        }
                    });
                }
            });
        };
        $scope.sendTestMessage = function() {
            //call the stoage API to save the APIKEY
            $http.post('credentialCheck', { 'username' : $scope.slackUsername }).then(function onSuccess(response) {
                var element = document.getElementById("sendTestMessageButton");
                element.className = "btn btn-success";
                element.innerHTML = "Check-Credentials <i class='fas fa-check'></i>";
                console.log("send test message");
                console.log(response);
            }, function onFailure(response) {
                $('#configWrong').modal('show');
                console.log(response);
                console.log("can't connect to slack");
            });
        };
        $scope.sendTestMessageSteps = function(userEmail) {
            //call the stoage API to save the APIKEY
            $http.post('credentialCheck', { 'username' : userEmail }).then(function onSuccess(response) {
                var element = document.getElementById("sendTestMessageButton");
                document.getElementById("slackbotuserinput").value = userEmail;
                element.className = "btn btn-success";
                element.innerHTML = "Check-Credentials <i class='fas fa-check'></i>";
                console.log("send test message");
                console.log(response);
            }, function onFailure(response) {
                $('#configWrong').modal('show');
                console.log(response);
                console.log("can't connect to slack");
            });
        };

        $scope.startSlackBot = function() {
            if(document.getElementById("startBotButton").className == "btn btn-success") {
                $('#serverStartedAlready').modal('show');
            }else{
                $scope.ApiKeyValueSaveToStorageAPI();
                //call the stoage API to save the APIKEY
                $http.get('startSlackBot').then(function onSuccess(response) {
                    var element = document.getElementById("startBotButton");
                    element.className = "btn btn-success";
                    element.innerHTML = "Start SlackBot <i class='fas fa-check'></i>";
                    console.log("start SlackBot");
                    console.log(response);
                }, function onFailure(response) {
                $('#configWrong').modal('show');
                console.log(response);
                console.log("can't connect to slack");
            });
            }
        };
        $scope.dialogFlowInitialize = function() {
                //call the stoage API to save the APIKEY
                $http.get('dialogFlowInitialize').then(function onSuccess(response) {
                    console.log("DialogFlow Initialized! That means be pushed the content of iolite to dialogflow");
                }, function onFailure(response) {
                    console.log(response);
                    console.log("can't connect to dialogflow");
                });
        };
        $scope.actionNextStep = function(currentStep) {
            //call the stoage API to save the APIKEY
            //var currentStep = document.getElementById("currentSteps").value;
            console.log(currentStep);
            if(currentStep == "1"){
                //slackbot setup
                console.log("currentStep0");
            }else if(currentStep == "2"){
                //Slackbot apikey add
                $scope.ApiKeyChanged = document.getElementById("inputAPIKEYSteps").value;
                $scope.ApiKeyValueSaveToStorageAPI();
                console.log("Slackbot apikey add");
            }else if(currentStep == "3"){
                //dialogflow api key
                var akikeyDialogflow = document.getElementById("inputAPIKEYDialogflowSteps").value;
                $scope.ApiKeyDialogFlowValueSaveToStorageAPI(akikeyDialogflow);
                console.log("dialogflow api key");
                $scope.dialogFlowInitialize();
            }else if(currentStep == "4"){
                //test message
                var slackUserEMail = document.getElementById("SlackUserEMail").value;
                $scope.sendTestMessageSteps(slackUserEMail);
                console.log("send test message");
            }else if(currentStep == "5"){
                console.log("start server");
                $scope.startSlackBot();
                $scope.isFontEndStepsDone();
                $scope.lastStep();
            }
        };

    }]);
})();