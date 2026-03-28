<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">产品经理全生命周期管理系统</h1>
      <p class="dashboard-subtitle">一站式产品管理平台，从需求到上线的全流程解决方案</p>
    </div>
    
    <div class="dashboard-grid">
      <!-- 模块1：产品全生命周期工作流 -->
      <div class="dashboard-module">
        <div class="module-header">
          <h2 class="module-title">产品全生命周期工作流</h2>
          <p class="module-subtitle">需求从0到1落地，全流程可追溯</p>
        </div>
        
        <div class="module-content">
          <div class="workflow-steps">
            <div class="step">
              <div class="step-icon">
                <el-icon><DocumentAdd /></el-icon>
              </div>
              <div class="step-label">需求收集</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="step-label">需求分析</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Edit /></el-icon>
              </div>
              <div class="step-label">产品设计</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Grid /></el-icon>
              </div>
              <div class="step-label">研发协同</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Check /></el-icon>
              </div>
              <div class="step-label">测试验收</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><SoldOut /></el-icon>
              </div>
              <div class="step-label">上线发布</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Refresh /></el-icon>
              </div>
              <div class="step-label">复盘迭代</div>
            </div>
          </div>
          
          <div class="module-highlights">
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><Flag /></el-icon>
              </div>
              <div class="highlight-text">优先级模型</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="highlight-text">PRD模板</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="highlight-text">变更管控</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 模块2：AI 辅助与能力成长 -->
      <div class="dashboard-module">
        <div class="module-header">
          <h2 class="module-title">AI 辅助与能力成长</h2>
          <p class="module-subtitle">AI 全流程赋能，边干边学提升能力</p>
        </div>
        
        <div class="module-content">
          <div class="workflow-steps">
            <div class="step">
              <div class="step-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="step-label">AI 需求拆解</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><DocumentCopy /></el-icon>
              </div>
              <div class="step-label">AI 文档生成</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="step-label">AI 会议辅助</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="step-label">理论课程</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><DocumentCopy /></el-icon>
              </div>
              <div class="step-label">案例模板</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Aim /></el-icon>
              </div>
              <div class="step-label">能力评估</div>
            </div>
          </div>
          
          <div class="module-highlights">
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><MapLocation /></el-icon>
              </div>
              <div class="highlight-text">场景化AI</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="highlight-text">提示词模板</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="highlight-text">大厂案例</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 模块3：个人知识库与资产沉淀 -->
      <div class="dashboard-module">
        <div class="module-header">
          <h2 class="module-title">个人知识库与资产沉淀</h2>
          <p class="module-subtitle">工作即沉淀，构建个人产品知识资产</p>
        </div>
        
        <div class="module-content">
          <div class="workflow-steps">
            <div class="step">
              <div class="step-icon">
                <el-icon><Folder /></el-icon>
              </div>
              <div class="step-label">文档归档</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><CollectionTag /></el-icon>
              </div>
              <div class="step-label">知识标签</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><PictureFilled /></el-icon>
              </div>
              <div class="step-label">作品集生成</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="step-label">踩坑经验</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Orange /></el-icon>
              </div>
              <div class="step-label">方法论</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Search /></el-icon>
              </div>
              <div class="step-label">知识检索</div>
            </div>
          </div>
          
          <div class="module-highlights">
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><MagicStick /></el-icon>
              </div>
              <div class="highlight-text">全自动归档</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><Grid /></el-icon>
              </div>
              <div class="highlight-text">一键作品集</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><Search /></el-icon>
              </div>
              <div class="highlight-text">智能检索</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 模块4：团队协同与效能管理 -->
      <div class="dashboard-module">
        <div class="module-header">
          <h2 class="module-title">团队协同与效能管理</h2>
          <p class="module-subtitle">跨团队高效协同，提升团队交付效能</p>
        </div>
        
        <div class="module-content">
          <div class="workflow-steps">
            <div class="step">
              <div class="step-icon">
                <el-icon><SoldOut /></el-icon>
              </div>
              <div class="step-label">需求提报</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="step-label">评审会议</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="step-label">迭代排期</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="step-label">风险管控</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><Operation /></el-icon>
              </div>
              <div class="step-label">资源调配</div>
            </div>
            <div class="step-connector"></div>
            <div class="step">
              <div class="step-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="step-label">效能分析</div>
            </div>
          </div>
          
          <div class="module-highlights">
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="highlight-text">多角色权限</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="highlight-text">风险预警</div>
            </div>
            <div class="highlight-item">
              <div class="highlight-icon">
                <el-icon><DataBoard /></el-icon>
              </div>
              <div class="highlight-text">效能看板</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  DocumentAdd,
  DataAnalysis,
  Edit,
  Check,
  SoldOut,
  Refresh,
  Flag,
  Document,
  Warning,
  Grid,
  DocumentCopy,
  VideoCamera,
  Reading,
  Aim,
  MapLocation,
  ChatDotRound,
  OfficeBuilding,
  Folder,
  CollectionTag,
  PictureFilled,
  Orange,
  Search,
  MagicStick,
  Calendar,
  Operation,
  UserFilled,
  DataBoard
} from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: {
    DocumentAdd,
    DataAnalysis,
    Edit,
    Check,
    SoldOut,
    Refresh,
    Flag,
    Document,
    Warning,
    Grid,
    DocumentCopy,
    VideoCamera,
    Reading,
    Aim,
    MapLocation,
    ChatDotRound,
    OfficeBuilding,
    Folder,
    CollectionTag,
    PictureFilled,
    Orange,
    Search,
    MagicStick,
    Calendar,
    Operation,
    UserFilled,
    DataBoard
  }
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  max-width: 1440px;
  margin: 0 auto;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 40px;
}

.dashboard-title {
  font-size: 32px;
  font-weight: 700;
  color: #1d2129;
  margin-bottom: 8px;
}

.dashboard-subtitle {
  font-size: 16px;
  color: #86909c;
  margin: 0;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.dashboard-module {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 24px;
  transition: all 0.3s ease;
}

.dashboard-module:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.module-header {
  margin-bottom: 24px;
}

.module-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
}

.module-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0;
}

.module-content {
  position: relative;
}

.workflow-steps {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 12px;
  position: relative;
}

.step-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  font-size: 20px;
  color: #165dff;
  transition: all 0.3s ease;
}

.step:hover .step-icon {
  background-color: #e6f0ff;
  transform: scale(1.1);
}

.step-label {
  font-size: 12px;
  color: #1d2129;
  text-align: center;
  max-width: 80px;
}

.step-connector {
  flex: 1;
  height: 2px;
  background-color: #e8e9ed;
  min-width: 20px;
}

.module-highlights {
  display: flex;
  gap: 16px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e9ed;
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: #f7f8fa;
  border-radius: 8px;
  border-left: 3px solid #165dff;
  flex: 1;
}

.highlight-icon {
  font-size: 16px;
  color: #165dff;
}

.highlight-text {
  font-size: 12px;
  font-weight: 500;
  color: #1d2129;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  
  .workflow-steps {
    justify-content: center;
  }
  
  .step {
    margin: 0 8px;
  }
  
  .step-label {
    font-size: 11px;
    max-width: 60px;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .dashboard-title {
    font-size: 24px;
  }
  
  .dashboard-module {
    padding: 16px;
  }
  
  .module-title {
    font-size: 18px;
  }
  
  .step-icon {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .step-label {
    font-size: 10px;
  }
  
  .module-highlights {
    flex-direction: column;
    gap: 12px;
  }
  
  .highlight-item {
    padding: 12px;
  }
}
</style>