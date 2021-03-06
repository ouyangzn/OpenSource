/*
 * Copyright (c) 2016.  ouyangzn   <email : ouyangzn@163.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ouyangzn.github.data.local;

import com.ouyangzn.github.bean.localbean.CollectedRepo;
import com.ouyangzn.github.dao.CollectedRepoDao;
import com.ouyangzn.github.dao.DaoSession;
import com.ouyangzn.github.data.ICollectDataSource;
import com.ouyangzn.github.db.DaoHelper;
import com.ouyangzn.github.utils.Log;
import java.util.List;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

/**
 * Created by ouyangzn on 2017/5/18.<br/>
 * Description：收藏的库数据源
 */
public class CollectLocalDataSourceSource implements ICollectDataSource {

  private final static String TAG = CollectLocalDataSourceSource.class.getSimpleName();

  private CollectedRepoDao mRepoDao;

  public CollectLocalDataSourceSource() {
    DaoSession daoSession = DaoHelper.getDaoSession();
    mRepoDao = daoSession.getCollectedRepoDao();
  }

  @Override public List<CollectedRepo> queryCollectRepo(int page, int limit) {
    return mRepoDao.queryBuilder()
        .limit(limit)
        .offset(page * limit)
        .orderDesc(CollectedRepoDao.Properties.CollectTime)
        .build()
        .list();
  }

  @Override public List<CollectedRepo> queryByKeyword(String keyword, int page, int limit) {
    WhereCondition fullName = CollectedRepoDao.Properties.FullName.like("%" + keyword + "%");
    WhereCondition desc = CollectedRepoDao.Properties.Description.like("%" + keyword + "%");
    QueryBuilder<CollectedRepo> qb = mRepoDao.queryBuilder();
    return qb.where(qb.or(fullName, desc)).limit(limit).offset(page * limit)
        .orderDesc(CollectedRepoDao.Properties.CollectTime)
        .list();
  }

  @Override public boolean collectRepo(CollectedRepo repo) {
    long rawId = mRepoDao.insertOrReplace(repo);
    return -1 != rawId;
  }

  @Override public boolean cancelRepo(CollectedRepo repo) {
    return cancelRepo(repo.getId());
  }

  @Override public boolean cancelRepo(Long id) {
    if (id == null) {
      Log.w(TAG, "cancelRepo failure, id == null");
      return false;
    }
    mRepoDao.deleteByKey(id);
    return true;
  }
}
