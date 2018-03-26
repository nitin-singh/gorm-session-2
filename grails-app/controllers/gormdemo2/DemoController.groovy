package gormdemo2

import gorm2.co.AccountSearchCO
import gorm2.co.UserSearchCO
import gorm2.vo.AccountInfoVO

class DemoController {

    static defaultAction = "list"

    def list(String q, Integer age) {
        List<User> users = User.findUsers(q, age)
        render "Result -> ${users.size()} ${users.firstName} ${users.age}"
    }

    def listPaginate(UserSearchCO co) {
        List<User> users = User.createCriteria().list(max: co.max ?: 10, offset: co.offset, order: co.order, sort: co.sort) {
            if (co.q) {
                ilike("firstName", "%${co.q}%")
            }
            if (co.age) {
                le("age", co.age)
            }
        }
        render "Result -> ${users.size()} ${users*.firstName} totalCount ${users.totalCount}"
    }

    def listDistinct() {
        List<User> users = User.createCriteria().listDistinct() {
            ilike("firstName", "Test 1%")
            le("age", 50)
            between("age", 18, 60)
            maxResults 10
            firstResult 0
            order("age", "desc")
        }
        render "Result ->${users.size()} ${users*.id}"
    }

    def nested() {
        List<Account> accounts = Account.createCriteria().list() {
            if (params.q) {
                'user' {
                    ilike("firstName", "%${params.q}%")

                }
            }
            le("balance", 10000)
            if (params.age) {
                'user' {
                    ge("age", params.age)
                }
            }
            maxResults 10
            firstResult 0
            'user' {
                order(params.sort ?: 'age', "desc")
            }
        }
        render "Result ->${accounts.size()} ${accounts.user.firstName}  ${accounts.user.age}"
    }

    def get() {
        User user = User.createCriteria().get {
            eq("id", 1L)
        }
        render user
    }

    def count() {
        List list = [10, 25, 30]
        Integer userCount = User.createCriteria().count() {
            ilike("firstName", "Test%")
            le("age", 30)
            if (list) {
                inList("age", list)
            }
        }
        render "Result -> ${userCount}"
    }

    def and() {
        List<Account> accounts = Account.createCriteria().list() {
            and {
                'branch' {
                    eq("name", "London")
                }
                or {
                    between("balance", 5000, 10000)
                    'user' {
                        ilike("firstName", "Test 2%")
                    }

                }
            }
        }
        render "Result -> ${accounts*.balance} ${accounts*.branch*.name} ${accounts*.user.firstName}"
    }

    def or() {
        List<Account> accounts = Account.createCriteria().list() {
            or {
                between("balance", 5000, 10000)
                'branch' {
                    eq("name", "London")
                }
            }
        }
        render "Result -> ${accounts*.balance} ${accounts*.branch*.name}"
    }

    def not() {
        List<Account> accounts = Account.createCriteria().list() {
            not {
                between("balance", 5000, 10000)

                'branch' {
                    eq("name", "London")
                }
            }
        }
        render "Result -> ${accounts*.balance} ${accounts*.branch*.name}"
    }

    def property() {
        def result = User.createCriteria().list {
            projections {
                property("age", 'userage')

            }
            ilike("firstName", "Test%")
            le("age", 50)
            between("age", 18, 60)

        }
        render "Result -> ${result}"
    }

    def distinct() {
        List<Integer> userAges = User.createCriteria().list() {
            projections {
                distinct("age")
            }
            ilike("firstName", "Test%")
            le("age", 50)
            between("age", 18, 60)
        }
        render "Result -> ${userAges}"
    }

    def projections() {
        Integer ageSum = User.createCriteria().get() {
            projections {
                sum("age")
            }
            ilike("firstName", "Test%")
            le("age", 50)
            between("age", 18, 60)
        }
        render "Result -> ${ageSum}"
    }

    def projectProperties() {
        User user = User.first()
        AccountInfoVO accountInfoVO = user.accountInfo
        render "${accountInfoVO}"
    }

    def groupProperty() {
        List result = Account.createCriteria().list() {
            projections {
                groupProperty("branch")
                sum("balance")
            }
        }
        render "Result -> ${result}"
    }

    def alias() {
        List result = Account.createCriteria().list() {
            projections {
                createAlias("branch", "b")
                groupProperty("b.id")
                property("b.name")
                sum("balance", 'totalBalance')
            }
            order("totalBalance", "desc")
            order("b.name", "desc")
        }
        render "Result -> ${result}"
    }

    def executeQuery() {
        Integer age = 19
        List usersInfo = User.executeQuery("select u.firstName, u.lastName from User as u where u.age >:test", [test: age])
        render "User Info -: ${usersInfo}"
    }

    def executeUpdate() {
        User user = User.get(1)
        String firstName = user.firstName
        User.executeUpdate("update User as u set u.firstName=:firstName where u.id=:id", [firstName: "Test", id: 1.toLong()])
//        user.refresh()
        render "firstName before ${firstName} -: After updation ${user.firstName}"
        User.executeUpdate("delete User where id=:id", [id: 1.toLong()])
//        render "Success"
    }


    def namedQuery(AccountSearchCO co) {
//        List<Account> accounts = Account.search(co).list()
//        List<Account> accounts = Account.search(co).list(max:co.max,order:co.order,sort:co.sort,offset:co.offset)
        Date date = new Date()
        List<Account> accounts = Account.search(co).findAllByDateCreatedLessThan(date - 1, [max: co.max, order: co.order, sort: co.sort, offset: co.offset])
        render "Success -> ${accounts.balance} -> ${Account.search(co).countByDateCreatedLessThan(date - 1)}"
//        render "Success -> ${accounts.branch} -> ${Account.search(co).count()}"
    }
}
