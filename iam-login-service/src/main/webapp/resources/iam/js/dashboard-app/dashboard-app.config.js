angular.module('dashboardApp').value('getUserInfo', getUserInfo);

angular
    .module('dashboardApp')
    .config(
        function($stateProvider, $urlRouterProvider, $httpProvider) {

            $httpProvider.interceptors.push('gatewayErrorInterceptor');
            $httpProvider.interceptors.push('sessionExpiredInterceptor');

            $urlRouterProvider.otherwise(function($injector, $location) {
                return '/home';
            });

            $stateProvider
                .state('home', {
                    url: '/home',
                    resolve: {
                        user: loadLoggedUser,
                        aup: loadAup
                    },
                    views: {
                        content: {
                            component: 'user'
                        }
                    }
                })
                .state('user', {
                    url: '/user/:id',
                    resolve: {
                        user: loadUser,
                        aup: loadAup
                    },
                    views: {
                        content: {
                            component: 'user'
                        }
                    }
                })
                .state(
                    'group', {
                        url: '/group/:id',
                        views: {
                            content: {
                                templateUrl: '/resources/iam/template/dashboard/group/group.html',
                                controller: 'GroupController',
                                controllerAs: 'group'
                            }
                        }
                    })
                .state(
                    'users', {
                        url: '/users',
                        views: {
                            content: {
                                templateUrl: '/resources/iam/template/dashboard/users/users.html',
                                controller: 'UsersController',
                                controllerAs: 'users'
                            }
                        }
                    })
                .state(
                    'groups', {
                        url: '/groups',
                        views: {
                            content: {
                                templateUrl: '/resources/iam/template/dashboard/groups/groups.html',
                                controller: 'GroupsController',
                                controllerAs: 'gc'
                            }
                        }
                    })
                .state(
                    'requests', {
                        url: '/requests',
                        views: {
                            content: {
                                templateUrl: '/resources/iam/template/dashboard/requests/management.html',
                                controller: 'RequestManagementController',
                                controllerAs: 'requests'
                            }
                        }
                    }).state('tokens', {
                    url: '/tokens',
                    resolve: {
                        clients: loadClients,
                        users: loadUsers
                    },
                    views: {
                        content: {
                            component: 'tokens'
                        }
                    }
                }).state('test', {
                    url: '/test/:id',
                    resolve: {
                        user: loadUser
                    },
                    views: {
                        content: {
                            component: 'test'
                        }
                    }
                }).state('aup', {
                    url: '/aup',
                    resolve: {
                        aup: loadAup
                    },
                    views: {
                        content: {
                            component: 'aup'
                        }
                    }
                });

            function loadLoggedUser(scimFactory) {
                return scimFactory.getMe().then(function(r) {
                    var user = r.data;
                    user.authorities = getUserAuthorities();
                    return user;
                });
            }

            function loadUser(UserService, $stateParams) {
                return UserService.getUser($stateParams.id);
            }

            function loadUsers(scimFactory) {
                return scimFactory.getAllUsers();
            }

            function loadClients(ClientsService) {
                return ClientsService.getClientList().then(function(r) {
                    return r.data;
                });
            }

            function loadAup(AupService) {
                return AupService.getAup();
            }
        });