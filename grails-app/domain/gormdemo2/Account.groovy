package gormdemo2

import gorm2.co.AccountSearchCO


class Account {

    Integer balance = 0
    Date dateCreated

    static belongsTo = [branch: Branch, user: User]

    static constraints = {
        dateCreated bindable: true
    }

    static mapping = {
        autoTimestamp false
    }

    static namedQueries = {
        search { AccountSearchCO co ->
            if (co.branch) {
                eq('branch', co.branch)
            }
            if (co.balance) {
                ge('balance', co.balance)
            }
        }
    }
}
