package gormdemo2

import gorm2.vo.AccountInfoVO


class User {

    String firstName
    String lastName
    String address
    Integer age

    static transients = ['accountInfo']
    static hasMany = [accounts: Account]

    AccountInfoVO getAccountInfo() {
        List result = Account.createCriteria().get {
            projections {
                count('id', 'accountCount')
                sum('balance')
                avg('balance')
            }
            eq('user', this)
            order('accountCount', 'desc')
        }

        new AccountInfoVO(totalAccounts: result[0], totalBalance: result[1], averageBalance: result[2])
    }

    static List<User> findUsers(String q, Integer age) {
        List<User> users = User.createCriteria().list() {
            ilike("firstName", "${q}%")
            ilike("address", "%${q}")
            le("age", age)
        }
        return users
    }

}
