var app = angular.module('app', []);
app.controller('wikiController', function($scope) {
    $scope.window = 'home';
    
    $scope.changeWindow = function(name){
        $scope.window = name;
    }
});


