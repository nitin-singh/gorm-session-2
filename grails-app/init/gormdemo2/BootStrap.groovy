package gormdemo2

class BootStrap {

    def init = { servletContext ->
        createBranches()
        createUsers()
        createAccounts()
    }

    void createBranches() {
        if (!Branch.count()) {
            (1..10).each {
                Branch branch = new Branch(name: "Delhi", address: "Address ${it}, Delhi")
                saveObject(branch)
                branch = new Branch(name: "London", address: "Address ${it}, London")
                saveObject(branch)
                branch = new Branch(name: "Berlin", address: "Address ${it}, Berlin")
                saveObject(branch)
                branch = new Branch(name: "New York", address: "Address ${it}, New York")
                saveObject(branch)
                branch = new Branch(name: "Sydney", address: "Address ${it}, Sydney")
                saveObject(branch)
            }
        }
    }

    void createUsers() {
        if (!User.count()) {
            User user
            (1..50).each {
                user = new User(firstName: "Test ${it}", address: "Address user ${it + 1}", lastName: "last name${it}",
                        age: it < 18 ? (it + 18) : ((it > 50) ? (it - 32) : it))
                saveObject(user)
            }
        }
    }

    void createAccounts() {
        if (!Account.count()) {
            User.list().eachWithIndex { User user, index ->
                Branch branch = Branch.get(user.id)
                Date date = (new Date() - index)
                3.times {
                    Account account = new Account(balance: 1000 * (user.id) * it, user: user, dateCreated: date, branch: branch)
                    saveObject(account)
                }
            }
        }
    }

    void saveObject(Object object) {
        if (!object.save(flush: true)) {
            object.errors.allErrors.each {
                println "Errror ${it}"
            }
        }
    }
}
