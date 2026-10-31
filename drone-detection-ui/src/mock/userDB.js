// src/mock/userDB.js

// 定义存储的键名
const STORAGE_KEY_USERS = 'drone_users';
const STORAGE_KEY_UNIVERSITY = 'drone_university';

// 默认用户数据（与原先一致）
const defaultUsers = {
  admin: {
    name: 'admin',
    password: '123456',
    email: 'admin@example.com',
    first: '王',
    second: '2006',
    third: '清华大学'
  },
  user: {
    name: 'user',
    password: '123456',
    email: 'user@example.com',
    first: '王',
    second: '2008',
    third: '清华大学'
  }
};

// 默认高校列表
const defaultUniversity = [
  '清华大学', '北京大学', '复旦大学', '上海交通大学',
  '浙江大学', '中国科学技术大学', '南京大学', '西安交通大学',
  '哈尔滨工业大学', '武汉大学', '华中科技大学', '中山大学',
  '四川大学', '南开大学', '天津大学', '山东大学', '电子科技大学'
];

// 从 localStorage 加载用户数据
function loadUsers() {
  const stored = localStorage.getItem(STORAGE_KEY_USERS);
  if (stored) {
    try {
      return JSON.parse(stored);
    } catch (e) {
      console.warn('解析用户数据失败，使用默认值', e);
    }
  }
  localStorage.setItem(STORAGE_KEY_USERS, JSON.stringify(defaultUsers));
  return defaultUsers;
}

// 从 localStorage 加载高校列表
function loadUniversity() {
  const stored = localStorage.getItem(STORAGE_KEY_UNIVERSITY);
  if (stored) {
    try {
      return JSON.parse(stored);
    } catch (e) {
      console.warn('解析高校列表失败，使用默认值', e);
    }
  }
  localStorage.setItem(STORAGE_KEY_UNIVERSITY, JSON.stringify(defaultUniversity));
  return defaultUniversity;
}

// 内部存储对象
let users = loadUsers();
const universityArray = loadUniversity();

// 保存用户数据
function saveUsers() {
  localStorage.setItem(STORAGE_KEY_USERS, JSON.stringify(users));
}

// 保存高校列表
function saveUniversity() {
  localStorage.setItem(STORAGE_KEY_UNIVERSITY, JSON.stringify(universityArray));
}

// 对外暴露的 userMap 对象（完全模拟 Map 的行为）
export const userMap = {
  /**
   * 判断用户名是否存在
   */
  has(username) {
    return Object.prototype.hasOwnProperty.call(users, username);
  },
  /**
   * 获取用户信息（返回代理对象，修改属性自动保存）
   */
  get(username) {
    const user = users[username];
    if (!user) return undefined;
    // 使用 Proxy 拦截属性设置，实现自动保存
    return new Proxy(user, {
      set(target, prop, value) {
        target[prop] = value;
        saveUsers(); // 属性修改立即持久化
        return true;
      }
    });
  },
  /**
   * 设置/添加用户
   */
  set(username, userData) {
    users[username] = userData;
    saveUsers();
  },
  /**
   * 删除用户
   */
  delete(username) {
    delete users[username];
    saveUsers();
  },
  /**
   * 获取所有用户对象（用于遍历）
   */
  values() {
    return Object.values(users); // 返回数组，支持 for...of
  },
  /**
   * 获取所有用户（返回对象，便于其他操作）
   */
  getAll() {
    return users;
  },
  /**
   * 返回用户数量
   */
  get size() {
    return Object.keys(users).length;
  },
  /**
   * 使对象可迭代（支持 for...of）
   */
  [Symbol.iterator]() {
    const entries = Object.entries(users);
    let index = 0;
    return {
      next: () => {
        if (index < entries.length) {
          return { value: entries[index++], done: false };
        } else {
          return { done: true };
        }
      }
    };
  }
};

// 对外暴露的高校列表（通过 Proxy 拦截修改操作，自动保存）
export const university = new Proxy(universityArray, {
  set(target, prop, value) {
    target[prop] = value;
    saveUniversity();
    return true;
  },
  deleteProperty(target, prop) {
    delete target[prop];
    saveUniversity();
    return true;
  }
});
